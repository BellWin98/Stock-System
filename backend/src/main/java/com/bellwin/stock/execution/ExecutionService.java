package com.bellwin.stock.execution;

import com.bellwin.stock.command.OrderRequestEvent;
import com.bellwin.stock.config.KafkaConfig;
import com.bellwin.stock.domain.Account;
import com.bellwin.stock.domain.AccountRepository;
import com.bellwin.stock.domain.Execution;
import com.bellwin.stock.domain.ExecutionRepository;
import com.bellwin.stock.domain.Order;
import com.bellwin.stock.domain.OrderRepository;
import com.bellwin.stock.domain.OrderSide;
import com.bellwin.stock.infra.DistributedLockExecutor;
import com.bellwin.stock.market.MarketQuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final ExecutionRepository executionRepository;
    private final DistributedLockExecutor lockExecutor;
    private final MarketQuoteService marketQuoteService;
    private final KafkaTemplate<String, OrderExecutedEvent> executedEventKafkaTemplate;

    public void processOrderRequest(OrderRequestEvent event) {
        String lockKey = "order:" + event.accountId() + ":" + event.symbol();
        try {
            lockExecutor.executeWithLock(lockKey, () -> {
                executeWithRetry(event);
                return null;
            });
        } catch (DistributedLockExecutor.LockAcquisitionException e) {
            log.warn("Lock failed for order {}: {}", event.orderId(), e.getMessage());
            rejectOrder(event.orderId());
        }
    }

    private void executeWithRetry(OrderRequestEvent event) {
        try {
            doExecute(event);
        } catch (OptimisticLockingFailureException e) {
            log.warn("Optimistic lock conflict, retrying order {}", event.orderId());
            doExecute(event);
        }
    }

    @Transactional
    protected void doExecute(OrderRequestEvent event) {
        Order order = orderRepository.findById(event.orderId()).orElse(null);
        if (order == null || order.getStatus() == com.bellwin.stock.domain.OrderStatus.FILLED
                || order.getStatus() == com.bellwin.stock.domain.OrderStatus.REJECTED) {
            return;
        }

        Account account = accountRepository.findById(event.accountId())
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        long fillAmount = (long) event.quantity() * event.price();

        if (event.side() == OrderSide.BUY) {
            account.deductCash(fillAmount);
        } else {
            account.addCash(fillAmount);
            account.unlockMargin(0);
        }

        order.markFilled();
        orderRepository.save(order);
        accountRepository.save(account);

        Execution execution = Execution.builder()
                .orderId(event.orderId())
                .symbol(event.symbol())
                .fillPrice(event.price())
                .fillQty(event.quantity())
                .build();
        executionRepository.save(execution);

        marketQuoteService.updateQuote(event.symbol(), event.price(), event.quantity());

        OrderExecutedEvent executedEvent = new OrderExecutedEvent(
                event.orderId(),
                event.symbol(),
                event.price(),
                event.quantity(),
                execution.getExecutedAt()
        );
        executedEventKafkaTemplate.send(KafkaConfig.ORDER_EXECUTED_TOPIC, event.orderId(), executedEvent);
        log.info("Order executed: {}", event.orderId());
    }

    @Transactional
    protected void rejectOrder(String orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getSide() == OrderSide.BUY) {
                accountRepository.findById(order.getAccountId()).ifPresent(account -> {
                    account.unlockMargin(order.orderAmount());
                    accountRepository.save(account);
                });
            }
            order.markRejected();
            orderRepository.save(order);
        });
    }
}

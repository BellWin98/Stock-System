package com.bellwin.stock.command;

import com.bellwin.stock.api.dto.CreateOrderRequest;
import com.bellwin.stock.api.dto.OrderResponse;
import com.bellwin.stock.domain.Account;
import com.bellwin.stock.domain.AccountRepository;
import com.bellwin.stock.domain.Instrument;
import com.bellwin.stock.domain.InstrumentRepository;
import com.bellwin.stock.domain.Order;
import com.bellwin.stock.domain.OrderRepository;
import com.bellwin.stock.domain.OrderSide;
import com.bellwin.stock.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final AccountRepository accountRepository;
    private final InstrumentRepository instrumentRepository;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderResponse submitOrder(Long accountId, CreateOrderRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        Instrument instrument = instrumentRepository.findById(request.symbol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown symbol"));

        Order order = Order.builder()
                .accountId(accountId)
                .symbol(request.symbol())
                .side(request.side())
                .quantity(request.quantity())
                .price(request.price())
                .build();

        long orderAmount = order.orderAmount();

        if (request.side() == OrderSide.BUY) {
            if (account.availableBalance() < orderAmount) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
            }
            account.lockMargin(orderAmount);
        }

        order.markAccepted();
        orderRepository.save(order);
        accountRepository.save(account);

        orderEventPublisher.publishOrderRequest(new OrderRequestEvent(
                order.getId(),
                accountId,
                order.getSymbol(),
                order.getSide(),
                order.getQuantity(),
                order.getPrice()
        ));

        return toResponse(order, instrument.getName());
    }

    private OrderResponse toResponse(Order order, String instrumentName) {
        return new OrderResponse(
                order.getId(),
                order.getAccountId(),
                order.getSymbol(),
                instrumentName,
                order.getSide(),
                order.getQuantity(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}

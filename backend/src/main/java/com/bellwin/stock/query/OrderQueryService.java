package com.bellwin.stock.query;

import com.bellwin.stock.api.dto.OrderResponse;
import com.bellwin.stock.domain.InstrumentRepository;
import com.bellwin.stock.domain.Order;
import com.bellwin.stock.domain.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final InstrumentRepository instrumentRepository;

    public List<OrderResponse> getOrders(Long accountId) {
        return orderRepository.findTop20ByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        String name = instrumentRepository.findById(order.getSymbol())
                .map(i -> i.getName())
                .orElse(order.getSymbol());
        return new OrderResponse(
                order.getId(),
                order.getAccountId(),
                order.getSymbol(),
                name,
                order.getSide(),
                order.getQuantity(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}

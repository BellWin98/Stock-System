package com.bellwin.stock.api.dto;

import com.bellwin.stock.domain.OrderSide;
import com.bellwin.stock.domain.OrderStatus;
import java.time.Instant;

public record OrderResponse(
        String id,
        Long accountId,
        String symbol,
        String instrumentName,
        OrderSide side,
        int quantity,
        long price,
        OrderStatus status,
        Instant createdAt
) {
}

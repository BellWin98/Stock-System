package com.bellwin.stock.command;

import com.bellwin.stock.domain.OrderSide;

public record OrderRequestEvent(
        String orderId,
        Long accountId,
        String symbol,
        OrderSide side,
        int quantity,
        long price
) {
}

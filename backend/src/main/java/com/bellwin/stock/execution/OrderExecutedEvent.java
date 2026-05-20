package com.bellwin.stock.execution;

import java.time.Instant;

public record OrderExecutedEvent(
        String orderId,
        String symbol,
        long fillPrice,
        int fillQty,
        Instant executedAt
) {
}

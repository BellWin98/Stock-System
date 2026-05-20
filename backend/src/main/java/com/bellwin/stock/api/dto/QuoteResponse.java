package com.bellwin.stock.api.dto;

import java.time.Instant;

public record QuoteResponse(
        String symbol,
        String name,
        long lastPrice,
        int volume,
        Instant updatedAt
) {
}

package com.bellwin.stock.api.dto;

public record AccountResponse(
        Long id,
        long cashBalance,
        long lockedMargin,
        long availableBalance
) {
}

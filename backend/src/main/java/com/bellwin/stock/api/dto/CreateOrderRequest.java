package com.bellwin.stock.api.dto;

import com.bellwin.stock.domain.OrderSide;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotBlank String symbol,
        @NotNull OrderSide side,
        @Min(1) int quantity,
        @Min(1) long price
) {
}

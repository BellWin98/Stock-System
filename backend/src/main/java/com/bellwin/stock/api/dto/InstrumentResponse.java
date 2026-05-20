package com.bellwin.stock.api.dto;

public record InstrumentResponse(String symbol, String name, long lastPrice) {
}

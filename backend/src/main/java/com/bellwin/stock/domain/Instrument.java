package com.bellwin.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instruments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Instrument {

    @Id
    @Column(length = 12)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long lastPrice;

    @Builder
    public Instrument(String symbol, String name, long lastPrice) {
        this.symbol = symbol;
        this.name = name;
        this.lastPrice = lastPrice;
    }

    public void updateLastPrice(long price) {
        this.lastPrice = price;
    }
}

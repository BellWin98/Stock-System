package com.bellwin.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "executions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String orderId;

    @Column(nullable = false, length = 12)
    private String symbol;

    @Column(nullable = false)
    private long fillPrice;

    @Column(nullable = false)
    private int fillQty;

    @Column(nullable = false)
    private Instant executedAt;

    @Builder
    public Execution(String orderId, String symbol, long fillPrice, int fillQty) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.fillPrice = fillPrice;
        this.fillQty = fillQty;
        this.executedAt = Instant.now();
    }
}

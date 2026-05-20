package com.bellwin.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false, length = 12)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private OrderSide side;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Version
    private Long version;

    @Builder
    public Order(Long accountId, String symbol, OrderSide side, int quantity, long price) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public long orderAmount() {
        return (long) quantity * price;
    }

    public void markAccepted() {
        this.status = OrderStatus.ACCEPTED;
    }

    public void markFilled() {
        this.status = OrderStatus.FILLED;
    }

    public void markRejected() {
        this.status = OrderStatus.REJECTED;
    }
}

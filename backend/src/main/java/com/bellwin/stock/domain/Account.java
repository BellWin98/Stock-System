package com.bellwin.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long cashBalance;

    @Column(nullable = false)
    private long lockedMargin;

    @Version
    private Long version;

    @Builder
    public Account(long cashBalance) {
        this.cashBalance = cashBalance;
        this.lockedMargin = 0;
    }

    public long availableBalance() {
        return cashBalance - lockedMargin;
    }

    public void lockMargin(long amount) {
        if (availableBalance() < amount) {
            throw new IllegalStateException("Insufficient balance");
        }
        lockedMargin += amount;
    }

    public void unlockMargin(long amount) {
        lockedMargin = Math.max(0, lockedMargin - amount);
    }

    public void deductCash(long amount) {
        cashBalance -= amount;
        lockedMargin = Math.max(0, lockedMargin - amount);
    }

    public void addCash(long amount) {
        cashBalance += amount;
    }
}

package ru.antalas.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

public class Account {
    private final Integer id;
    private final BigDecimal balance;

    public Account(Integer id, BigDecimal balance) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(balance);
        Preconditions.checkArgument(balance.signum() != -1);

        this.id = id;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

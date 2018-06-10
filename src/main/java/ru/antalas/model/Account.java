package ru.antalas.model;

import java.math.BigDecimal;

public class Account {
    private final Integer id;
    private final BigDecimal balance;

    public Account(Integer id, BigDecimal balance) {
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

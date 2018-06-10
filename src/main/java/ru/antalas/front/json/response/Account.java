package ru.antalas.front.json.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Account {
    private final Integer id;
    private final BigDecimal balance;

    @JsonCreator
    public Account(@JsonProperty("id") Integer id, @JsonProperty("balance") BigDecimal balance) {
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

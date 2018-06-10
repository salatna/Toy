package ru.antalas.front.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AccountDto {
    private final BigDecimal balance;

    @JsonCreator
    public AccountDto(@JsonProperty("balance") BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

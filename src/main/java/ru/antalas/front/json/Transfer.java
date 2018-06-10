package ru.antalas.front.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Transfer {
    private final Integer sourceAccountId;
    private final Integer destinationAccountId;
    private final BigDecimal amount;

    @JsonCreator
    public Transfer(@JsonProperty Integer sourceAccountId, @JsonProperty Integer destinationAccountId, @JsonProperty BigDecimal amount) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    public Integer getSourceAccountId() {
        return sourceAccountId;
    }

    public Integer getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

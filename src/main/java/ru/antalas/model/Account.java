package ru.antalas.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class Account implements Comparable<Account> {
    private final Integer id;

    private BigDecimal balance;

    @JsonCreator
    public Account(@JsonProperty("id") Integer id, @JsonProperty("balance") BigDecimal balance) {
        checkNotNull(id);
        checkNotNull(balance);

        checkNonNegative(balance);
        checkCentsSpecified(balance);

        this.id = id;
        this.balance = balance;
    }

    public Account withdraw(BigDecimal amount) {
        checkNotNull(amount);

        checkPositive(amount);
        checkCentsSpecified(amount);

        BigDecimal updated = balance.subtract(amount);

        checkOverdraft(updated);

        balance = updated;
        return this;
    }

    public Account deposit(BigDecimal amount) {
        checkNotNull(amount);

        checkPositive(amount);
        checkCentsSpecified(amount);

        balance = balance.add(amount);
        return this;
    }

    private static void checkPositive(BigDecimal value) {
        if (value.signum() != 1) {
            throw new ModelException(value.toPlainString() + " is nonpositive.");
        }
    }

    private static void checkNonNegative(BigDecimal value) {
        if (value.signum() == -1) {
            throw new ModelException(value.toPlainString());
        }
    }

    private static void checkCentsSpecified(BigDecimal value) {
        if (value.scale() != 2) {
            throw new ModelException(value.toPlainString() + " no cents given.");
        }
    }

    private static void checkOverdraft(BigDecimal value) {
        if (value.signum() == -1) {
            throw new ModelException("Overdraft, check balance.");
        }
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Account o) {
        return Comparator.<Integer>naturalOrder().compare(id, o.id);
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}

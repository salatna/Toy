package ru.antalas.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.antalas.model.exceptions.NegativeAmountException;
import ru.antalas.model.exceptions.NonPositiveAmountException;
import ru.antalas.model.exceptions.NotInCentsException;
import ru.antalas.model.exceptions.OverdraftException;

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

        if (balance.signum() == -1) {
            throw new NegativeAmountException(balance.toPlainString());
        }

        if (balance.scale() != 2) {
            throw new NotInCentsException(balance.toPlainString());
        }

        this.id = id;
        this.balance = balance;
    }

    public Account withdraw(BigDecimal amount) {
        checkNotNull(amount);

        if (amount.signum() != 1) {
            throw new NonPositiveAmountException(amount.toPlainString());
        }

        if (amount.scale() != 2) {
            throw new NotInCentsException(amount.toPlainString());
        }

        BigDecimal updated = balance.subtract(amount);

        if (updated.signum() == -1) {
            throw new OverdraftException();
        }

        balance = updated;
        return this;
    }

    public Account deposit(BigDecimal amount) {
        checkNotNull(amount);

        if (amount.signum() != 1) {
            throw new NonPositiveAmountException(amount.toPlainString());
        }

        if (amount.scale() != 2) {
            throw new NotInCentsException(amount.toPlainString());
        }

        balance = balance.add(amount);
        return this;
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

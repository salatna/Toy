package ru.antalas.model;

import ru.antalas.model.exceptions.OverdraftException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Account implements Comparable<Account>{
    private final Integer id;

    private BigDecimal balance;

    public Account(Integer id, BigDecimal balance) {
        checkNotNull(id);
        checkNotNull(balance);
        checkArgument(balance.signum() != -1);
        checkArgument(balance.scale() == 2);

        this.id = id;
        this.balance = balance;
    }

    public Account withdraw(BigDecimal amount) {
        checkNotNull(amount);
        checkArgument(amount.signum() == 1);
        checkArgument(amount.scale() == 2);

        BigDecimal updated = balance.subtract(amount);

        if (updated.signum() == -1){
            throw new OverdraftException("Account " + id + " overdrawn.");
        }

        balance = updated;
        return this;
    }

    public Account deposit(BigDecimal amount) {
        checkNotNull(amount);
        checkArgument(amount.signum() == 1);
        checkArgument(amount.scale() == 2);

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

package ru.antalas.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.antalas.model.exceptions.OverdraftException;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

public class AccountTest {
    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void shouldCreate() throws Exception {
        assertThat(acc(1, bd("1.00")), is(acc(1, bd("1.00"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldErrWhenBalanceNegative() throws Exception {
        acc(1, bd("1.00").negate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldErrWhenBalanceNoCents() throws Exception {
        acc(1, bd("0"));
    }

    @Test
    public void shouldWithdraw() throws Exception {
        assertThat(acc(1, bd("10.00")).withdraw(bd("1.00")), is(acc(1, bd("9.00"))));
    }

    @Test
    public void shouldErrWhenOverdraft() throws Exception {
        expectedException.expect(OverdraftException.class);
        expectedException.expectMessage("Account 1 overdrawn.");

        acc(1, bd("1.00")).withdraw(bd("10.00"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldErrWhenWithdrawNoCents() throws Exception {
        acc(1, bd("10.00")).withdraw(bd("1"));
    }

    @Test
    public void shouldDeposit() throws Exception {
        assertThat(acc(1, bd("10.00")).deposit(bd("1.00")), is(acc(1, bd("11.00"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldErrWhenDepositNoCents() throws Exception {
        acc(1, bd("10.00")).deposit(bd("1"));
    }

    @Test
    public void shouldCompare() throws Exception {
        assertThat(acc(1, bd("1.00")).compareTo(acc(1, bd("1.00"))), is(0));
        assertThat(acc(1, bd("1.00")).compareTo(acc(2, bd("1.00"))), is(-1));
        assertThat(acc(2, bd("1.00")).compareTo(acc(1, bd("1.00"))), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void shouldErrOnNullCompare() throws Exception {
        acc(1, bd("1.00")).compareTo(null);
    }

    private static Account acc(Integer id, BigDecimal amount) {
        return new Account(id, amount);
    }

    private static BigDecimal bd(String number) {
        return new BigDecimal(number);
    }
}
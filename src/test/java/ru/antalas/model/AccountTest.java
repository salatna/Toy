package ru.antalas.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AccountTest {
    @Test
    public void shouldCreate() throws Exception {
        assertThat(acc(1, bd("1.00")), is(acc(1, bd("1.00"))));
    }

    @Test(expected = ModelException.class)
    public void shouldErrWhenBalanceNegative() throws Exception {
        acc(1, bd("1.00").negate());
    }

    @Test(expected = ModelException.class)
    public void shouldErrWhenBalanceNoCents() throws Exception {
        acc(1, bd("0"));
    }

    @Test
    public void shouldWithdraw() throws Exception {
        assertThat(acc(1, bd("10.00")).withdraw(bd("1.00")), is(acc(1, bd("9.00"))));
    }

    @Test(expected = ModelException.class)
    public void shouldErrWhenOverdraft() throws Exception {
        acc(1, bd("1.00")).withdraw(bd("10.00"));
    }

    @Test(expected = ModelException.class)
    public void shouldErrWhenWithdrawNoCents() throws Exception {
        acc(1, bd("10.00")).withdraw(bd("1"));
    }

    @Test
    public void shouldDeposit() throws Exception {
        assertThat(acc(1, bd("10.00")).deposit(bd("1.00")), is(acc(1, bd("11.00"))));
    }

    @Test(expected = ModelException.class)
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
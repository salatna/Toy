package ru.antalas.back.persistence;

import org.junit.Before;
import org.junit.Test;
import ru.antalas.model.Account;

import java.math.BigDecimal;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersistenceTest {

    private Persistence persistence;

    @Before
    public void setUp() throws Exception {
        persistence = new Persistence();

        persistence.create(new Account(1, new BigDecimal("100.00")));
        persistence.create(new Account(2, new BigDecimal("0.00")));
    }

    @Test
    public void shouldGetAmountForAccount() throws Exception {
        assertThat(persistence.amountAt(1), is(of(new BigDecimal("100.00"))));
        assertThat(persistence.amountAt(2), is(of(new BigDecimal("0.00"))));
    }

    @Test
    public void shouldReportMissingAccount() throws Exception {
        assertThat(persistence.amountAt(3), is(empty()));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldErrWhenAccountExistsAtCreation() throws Exception {
        persistence.create(new Account(1, new BigDecimal("42.00")));
    }

    @Test
    public void shouldTransfer() throws Exception {
        persistence.transfer(1, 2, new BigDecimal("100.00"));

        assertThat(persistence.amountAt(1), is(of(new BigDecimal("0.00"))));
        assertThat(persistence.amountAt(2), is(of(new BigDecimal("100.00"))));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldErrTransferWhenDestinationMissing() throws Exception {
        persistence.transfer(1, 3, new BigDecimal("100.00"));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldErrTransferWhenSourceMissing() throws Exception {
        persistence.transfer(3, 1, new BigDecimal("100.00"));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldErrTransferWhenBothMissing() throws Exception {
        persistence.transfer(3, 4, new BigDecimal("100.00"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldErrTransferWhenSameAccount() throws Exception {
        persistence.transfer(1, 1, new BigDecimal("100.00"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldErrTransferWhenZeroAmount() throws Exception {
        persistence.transfer(1, 2, new BigDecimal("0.00"));
    }
}
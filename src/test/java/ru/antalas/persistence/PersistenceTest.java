package ru.antalas.persistence;

import org.junit.Before;
import org.junit.Test;
import ru.antalas.model.Account;
import ru.antalas.model.ModelException;

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

        persistence.createAccount(new BigDecimal("100.00"));
        persistence.createAccount(new BigDecimal("0.00"));
    }

    @Test
    public void shouldCreateAccount() throws Exception {
        assertThat(persistence.createAccount(new BigDecimal("100.00")), is(new Account(3, new BigDecimal("100.00"))));
    }

    @Test
    public void shouldNotIncrementSequenceWhenCreationErrs() throws Exception {
        //noinspection EmptyCatchBlock
        try {
            persistence.createAccount(new BigDecimal("1"));
        }catch (Exception e){}

        assertThat(persistence.createAccount(new BigDecimal("1.00")), is(new Account(3, new BigDecimal("1.00"))));

    }

    @Test
    public void shouldGetAmountForAccount() throws Exception {
        assertThat(persistence.getAccount(1), is(of(new Account(1, new BigDecimal("100.00")))));
        assertThat(persistence.getAccount(2), is(of(new Account(2, new BigDecimal("0.00")))));
    }

    @Test
    public void shouldReportMissingAccount() throws Exception {
        assertThat(persistence.getAccount(3), is(empty()));
    }

    @Test
    public void shouldTransfer() throws Exception {
        persistence.transfer(1, 2, new BigDecimal("100.00"));

        assertThat(persistence.getAccount(1), is(of(new Account(1, new BigDecimal("0.00")))));
        assertThat(persistence.getAccount(2), is(of(new Account(2, new BigDecimal("100.00")))));
    }

    @Test(expected = ModelException.class)
    public void shouldErrTransferWhenDestinationMissing() throws Exception {
        persistence.transfer(1, 3, new BigDecimal("100.00"));
    }

    @Test(expected = ModelException.class)
    public void shouldErrTransferWhenSourceMissing() throws Exception {
        persistence.transfer(3, 1, new BigDecimal("100.00"));
    }

    @Test(expected = ModelException.class)
    public void shouldErrTransferWhenBothMissing() throws Exception {
        persistence.transfer(3, 4, new BigDecimal("100.00"));
    }

    @Test(expected = ModelException.class)
    public void shouldErrTransferWhenSameAccount() throws Exception {
        persistence.transfer(1, 1, new BigDecimal("100.00"));
    }

    @Test(expected = ModelException.class)
    public void shouldErrTransferWhenZeroAmount() throws Exception {
        persistence.transfer(1, 2, new BigDecimal("0.00"));
    }
}
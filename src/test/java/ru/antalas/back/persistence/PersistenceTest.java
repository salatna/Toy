package ru.antalas.back.persistence;

import org.junit.Before;
import org.junit.Test;
import ru.antalas.model.Account;

import java.math.BigDecimal;

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
        assertThat(persistence.amountAt(1), is(new BigDecimal("100.00")));
        assertThat(persistence.amountAt(2), is(new BigDecimal("0.00")));
    }
}
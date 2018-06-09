package ru.antalas.front.json;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MapperTest {

    private Mapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new Mapper();
    }

    @Test
    public void shouldParseMapToJSON() throws Exception {
        assertThat(mapper.json(of("key", "value")), is("{\"key\":\"value\"}"));
        assertThat(mapper.json(of("key", new BigDecimal("100.0"))), is("{\"key\":100.0}"));
        assertThat(mapper.json(of("key", new BigDecimal("100.00"))), is("{\"key\":100.00}"));
    }
}
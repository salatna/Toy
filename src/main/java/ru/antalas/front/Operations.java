package ru.antalas.front;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;

public class Operations {
    public static Object account(BigDecimal amount){
        return ImmutableMap.of("amount", amount);
    }

    public static Object accountNotFound(String item) {
        return ImmutableMap.of("not found", item);
    }
}

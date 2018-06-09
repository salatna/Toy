package ru.antalas.front;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;

class Operations {
    static Object account(BigDecimal amount){
        return ImmutableMap.of("amount", amount);
    }

    static Object accountNotFound(String id) {
        return ImmutableMap.of("id", id);
    }
}

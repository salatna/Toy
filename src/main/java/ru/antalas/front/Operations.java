package ru.antalas.front;

import com.google.common.collect.ImmutableMap;
import ru.antalas.model.Account;

import java.math.BigDecimal;

class Operations {
    static Object account(Account account) {
        return ImmutableMap.of("accountURI", accountURIFrom(account.getId()));
    }

    private static String accountURIFrom(Integer id) {
        return Routes.ACCOUNT.getPath().replace("{id}", String.valueOf(id));
    }

    static Object account(BigDecimal amount){
        return ImmutableMap.of("amount", amount);
    }

    static Object accountNotFound(String id) {
        return ImmutableMap.of("id", id);
    }
}

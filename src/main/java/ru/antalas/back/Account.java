package ru.antalas.back;

import ru.antalas.back.persistence.Persistence;

import java.math.BigDecimal;
import java.util.Optional;

public class Account {
    public static ru.antalas.model.Account account(Persistence data, String id, String amount) {
        ru.antalas.model.Account account = new ru.antalas.model.Account(Integer.valueOf(id), new BigDecimal(amount));
        data.create(account);
        return account;
    }

    public static Optional<BigDecimal> amount(Persistence data, String item) {
        Integer accountId = Integer.valueOf(item);
        return data.amountAt(accountId);
    }

    public static void transfer(Persistence data, String src, String dst, String amt) {
        Integer srcAccountId = Integer.valueOf(src);
        Integer dstAccountId = Integer.valueOf(dst);
        BigDecimal amount = new BigDecimal(amt);
        data.transfer(srcAccountId, dstAccountId, amount);
    }
}

package ru.antalas.back;

import ru.antalas.back.persistence.Persistence;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Account {
    private static final AtomicInteger accountIdSequence = new AtomicInteger();

    public static ru.antalas.model.Account account(Persistence data, ru.antalas.front.json.Account input) {
        int accountId = accountIdSequence.incrementAndGet();

        ru.antalas.model.Account account = new ru.antalas.model.Account(accountId, input.getAmount());
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

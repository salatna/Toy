package ru.antalas.back;

import ru.antalas.back.persistence.Persistence;

import java.math.BigDecimal;
import java.util.Optional;

import static java.lang.Integer.parseInt;

public class Account {
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

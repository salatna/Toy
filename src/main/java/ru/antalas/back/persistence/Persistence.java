package ru.antalas.back.persistence;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Persistence {
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();

    public void create(ru.antalas.model.Account account) {
        throw new RuntimeException("implement me");
    }

    public Optional<BigDecimal> amountAt(Integer id) {
        throw new RuntimeException("implement me");
    }

    public void transfer(Integer srcId, Integer dstId, BigDecimal amount) {
        throw new RuntimeException("implement me");
    }

    private static class Account {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        private final ru.antalas.model.Account data;

        public Account(ru.antalas.model.Account data) {
            this.data = data;
        }
    }
}

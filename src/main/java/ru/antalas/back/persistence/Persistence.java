package ru.antalas.back.persistence;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Persistence {
    //equivalent isolation level: SERIALIZED
    private final ReadWriteLock accountsLock = new ReentrantReadWriteLock(true);
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();

    public void create(ru.antalas.model.Account account) {
        Lock lock = accountsLock.writeLock();
        try {
            lock.lock();

            if (accounts.containsKey(account.getId())) {
                throw new IllegalStateException();
            }

            accounts.put(account.getId(), new Account(account));
        } finally {
            lock.unlock();
        }
    }

    public Optional<BigDecimal> amountAt(Integer id) {
        Lock collectionLock = accountsLock.readLock();
        try {
            collectionLock.lock();

            if (accounts.containsKey(id)) {
                Account data = accounts.get(id);
                Lock itemLock = data.lock.readLock();
                try {
                    itemLock.lock();
                    return of(data.data.getBalance());
                } finally {
                    itemLock.unlock();
                }
            } else {
                return empty();
            }
        } finally {
            collectionLock.unlock();
        }
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

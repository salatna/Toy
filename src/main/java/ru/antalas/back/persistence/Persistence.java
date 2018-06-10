package ru.antalas.back.persistence;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Optional.empty;

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
                    return Optional.of(data.data.getBalance());
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
        if (srcId.equals(dstId)) {
            throw new IllegalArgumentException();
        }

        Lock collectionLock = accountsLock.readLock();
        try {
            collectionLock.lock();

            if (accounts.containsKey(srcId) && accounts.containsKey(dstId)) {
                Account srcData = accounts.get(srcId);
                Account dstData = accounts.get(dstId);

                try {
                    for (Account account : srcData.inLockOrder(dstData)) {
                        account.lock.writeLock().lock();
                    }

                    srcData.data.withdraw(amount);
                    dstData.data.deposit(amount);
                } finally {
                    for (Account account : srcData.inLockOrder(dstData).reverse()) {
                        account.lock.writeLock().unlock();
                    }
                }
            } else {
                throw new IllegalStateException();
            }
        } finally {
            collectionLock.unlock();
        }
    }

    private static class Account implements Comparable<Account> {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        private final ru.antalas.model.Account data;

        Account(ru.antalas.model.Account data) {
            this.data = data;
        }

        ImmutableList<Account> inLockOrder(Account other) {
            return this.compareTo(other) > 0 ? of(other, this) : of(this, other);
        }

        @Override
        public int compareTo(Account o) {
            return this.data.compareTo(o.data);
        }
    }
}

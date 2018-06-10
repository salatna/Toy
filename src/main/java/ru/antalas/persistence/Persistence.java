package ru.antalas.persistence;

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
    //protected by accountsLock
    private int accountIdSequence = 0;

    //equivalent isolation level: SERIALIZED
    private final ReadWriteLock accountsLock = new ReentrantReadWriteLock(true);
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();

    public ru.antalas.model.Account createAccount(BigDecimal balance) {
        Lock lock = accountsLock.writeLock();
        try {
            lock.lock();

            accountIdSequence++;

            ru.antalas.model.Account data = new ru.antalas.model.Account(accountIdSequence, balance);
            accounts.put(data.getId(), new Account(data));
            return data;
        } finally {
            lock.unlock();
        }
    }

    public Optional<ru.antalas.model.Account> getAccount(Integer id) {
        Lock collectionLock = accountsLock.readLock();
        try {
            collectionLock.lock();

            if (accounts.containsKey(id)) {
                Account entry = accounts.get(id);
                Lock itemLock = entry.lock.readLock();
                try {
                    itemLock.lock();
                    return Optional.of(entry.data);
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
                Account srcEntry = accounts.get(srcId);
                Account dstEntry = accounts.get(dstId);

                try {
                    for (Account entry : srcEntry.inLockOrder(dstEntry)) {
                        entry.lock.writeLock().lock();
                    }

                    srcEntry.data.withdraw(amount);
                    dstEntry.data.deposit(amount);

                } finally {
                    for (Account account : srcEntry.inLockOrder(dstEntry).reverse()) {
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
        public int compareTo(@SuppressWarnings("NullableProblems") Account o) {
            return this.data.compareTo(o.data);
        }
    }
}

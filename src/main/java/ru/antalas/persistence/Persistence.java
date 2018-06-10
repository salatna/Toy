package ru.antalas.persistence;

import com.google.common.collect.ImmutableList;
import ru.antalas.model.ModelException;

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
    private int accountIdSequence = 1;

    //equivalent isolation level: SERIALIZED
    private final ReadWriteLock accountsLock = new ReentrantReadWriteLock(true);
    private final Map<Integer, AccountEntry> accounts = new ConcurrentHashMap<>();

    public ru.antalas.model.Account createAccount(BigDecimal balance) {
        Lock lock = accountsLock.writeLock();
        try {
            lock.lock();

            ru.antalas.model.Account data = new ru.antalas.model.Account(accountIdSequence, balance);

            accountIdSequence++;

            accounts.put(data.getId(), new AccountEntry(data));
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
                AccountEntry entry = accounts.get(id);
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
            throw new ModelException("Same account");
        }

        Lock collectionLock = accountsLock.readLock();
        try {
            collectionLock.lock();

            if (accounts.containsKey(srcId) && accounts.containsKey(dstId)) {
                AccountEntry srcEntry = accounts.get(srcId);
                AccountEntry dstEntry = accounts.get(dstId);

                try {
                    for (AccountEntry entry : srcEntry.inLockOrder(dstEntry)) {
                        entry.lock.writeLock().lock();
                    }

                    srcEntry.data.withdraw(amount);
                    dstEntry.data.deposit(amount);

                } finally {
                    for (AccountEntry entry : srcEntry.inLockOrder(dstEntry).reverse()) {
                        entry.lock.writeLock().unlock();
                    }
                }
            } else {
                throw new ModelException(notFoundMessageFrom(srcId, dstId));
            }
        } finally {
            collectionLock.unlock();
        }
    }

    private String notFoundMessageFrom(Integer srcId, Integer dstId) {
        if (accounts.containsKey(srcId)){
            return dstId + " not found.";
        }
        if (accounts.containsKey(dstId)){
            return srcId + " not found.";
        }

        return srcId + " and " + dstId + " not found.";
    }

    private static class AccountEntry implements Comparable<AccountEntry> {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        private final ru.antalas.model.Account data;

        AccountEntry(ru.antalas.model.Account data) {
            this.data = data;
        }

        ImmutableList<AccountEntry> inLockOrder(AccountEntry other) {
            return this.compareTo(other) > 0 ? of(other, this) : of(this, other);
        }

        @Override
        public int compareTo(@SuppressWarnings("NullableProblems") AccountEntry o) {
            return this.data.compareTo(o.data);
        }
    }
}

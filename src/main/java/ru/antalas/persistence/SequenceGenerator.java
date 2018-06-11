package ru.antalas.persistence;

import java.util.concurrent.atomic.AtomicInteger;

public interface SequenceGenerator {
    Integer next();

    class AtomicIntegerSequence implements SequenceGenerator {
        private final AtomicInteger seq = new AtomicInteger(1);

        @Override
        public Integer next() {
            return seq.getAndIncrement();
        }
    }
}

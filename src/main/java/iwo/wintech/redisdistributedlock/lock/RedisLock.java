package iwo.wintech.redisdistributedlock.lock;

import jakarta.validation.constraints.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class RedisLock implements Lock {
    private static final long DEFAULT_WAIT = 5L;
    private final String key;
    private final Duration duration;
    private final BiPredicate<String, Duration> acquireLock;
    private final Consumer<String> releaseLock;

    private boolean lockAcquired = false;

    public RedisLock(final String key,
                     final Duration duration,
                     final BiPredicate<String, Duration> acquireLock,
                     final Consumer<String> releaseLock
                     ) {
        this.key = key;
        this.duration = duration;
        this.acquireLock = acquireLock;
        this.releaseLock = releaseLock;
    }

    @Override
        public void lock() {
            while (!tryLock()) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(DEFAULT_WAIT));
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            final Clock clock = Clock.systemUTC();
            final long timeout = TimeUnit.MILLISECONDS.toNanos(DEFAULT_WAIT);
            final long finishTime = clock.millis() + timeout;

            while (!tryLock()) {
                if (clock.millis() > finishTime) {
                    throw new InterruptedException("");
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(DEFAULT_WAIT));
            }
        }

        @Override
        public boolean tryLock() {
            if (lockAcquired) {
                return true;
            }
            lockAcquired = acquireLock.test(key, duration);
            return lockAcquired;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit) {
            final Clock clock = Clock.systemUTC();
            final long timeout = unit.toMillis(time);
            final long finishTime = clock.millis() + timeout;

            while (!tryLock()) {
                if (clock.millis() > finishTime) {
                    return false;
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(DEFAULT_WAIT));
            }
            return true;
        }

        @Override
        public void unlock() {
            if (lockAcquired) {
                releaseLock.accept(key);
                lockAcquired = false;
            }
        }

        @Override
        public @NotNull Condition newCondition() {
            return new ReentrantLock().newCondition();
        }
}

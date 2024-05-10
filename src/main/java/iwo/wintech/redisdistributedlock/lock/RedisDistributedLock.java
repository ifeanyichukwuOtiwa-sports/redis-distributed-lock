package iwo.wintech.redisdistributedlock.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
@Component
public class RedisDistributedLock implements DistributedLock {

    private final RedisTemplate<String, byte[]> redisTemplate;

    @Override
    public boolean isLocked(final String key) {
        return redisTemplate.opsForValue().get(key) != null;
    }

    @Override
    public void lockAndExec(final String key, final Duration lockDuration, final Runnable runnable) {
        final Lock lock = createLock(key, lockDuration);
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T lockAndCall(final String key, final Duration lockDuration, final Callable<T> callable) throws Exception {
        final Lock lock = createLock(key, lockDuration);
        lock.lock();
        try {
            return callable.call();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Lock createLock(final String key, final Duration duration) {
        return new RedisLock(key, duration, this::acquireLock, this::releaseLock);
    }

    private Boolean acquireLock(final String key, final Duration duration) {
        final Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "redis-lock".getBytes(), duration);
        return Boolean.TRUE.equals(result);
    }

    private void releaseLock(final String key) {
        redisTemplate.delete(key);
    }
}

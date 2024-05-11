package iwo.wintech.redisdistributedlock.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
@Component
@Slf4j
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
            logInfo();
            runnable.run();
        } finally {
            lock.unlock();
            releaseLogInfo();
        }
    }

    @Override
    public <T> T lockAndCall(final String key, final Duration lockDuration, final Callable<T> callable) throws Exception {
        final Lock lock = createLock(key, lockDuration);
        lock.lock();
        try {
            logInfo();
            return callable.call();
        } finally {
            lock.unlock();
            releaseLogInfo();
        }
    }

    private static void releaseLogInfo() {
        log.info("releasing  lock");
    }

    private void logInfo() {
        log.info("Executing under redis Lock");
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

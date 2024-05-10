package iwo.wintech.redisdistributedlock.lock;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

public interface DistributedLock {
    boolean isLocked(String key);
    void lockAndExec(String key, Duration lockDuration, Runnable runnable);
    <T> T lockAndCall(String key, Duration lockDuration, Callable<T> callable) throws Exception;
    Lock createLock(String key, Duration duration);
}

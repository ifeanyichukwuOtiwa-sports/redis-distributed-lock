package iwo.wintech.redisdistributedlock.lock.aspects;

import iwo.wintech.redisdistributedlock.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class RedisAspectsHandler {
    private final DistributedLock distributedLock;

    @Pointcut("@annotation(iwo.wintech.redisdistributedlock.lock.aspects.RedisLockAspect)")
    public void redisLock() {}

    @Around("@annotation(redisLockAspect)")
    public Object around(final ProceedingJoinPoint jp, final RedisLockAspect redisLockAspect) throws Exception {
        final String key = redisLockAspect.key();
        final Duration lockDuration = Duration.ofSeconds(redisLockAspect.lockSeconds());
        try {
            distributedLock.lockAndCall(key, lockDuration, () -> {
                try {
                    log.info("Executing under redis Lock");
                    return jp.proceed();
                } catch (final Throwable e) {
                    log.error("Error while executing method: {}", jp.getSignature(), e);
                    throw new RuntimeException(e);
                }
            });
        } catch (final Throwable throwable) {
            log.error("Error while executing method: {}", jp.getSignature(), throwable);
            throw throwable;
        }
        return null;
    }
}

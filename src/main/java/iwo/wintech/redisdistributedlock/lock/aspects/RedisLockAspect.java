package iwo.wintech.redisdistributedlock.lock.aspects;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD})
public @interface RedisLockAspect {
    String name() default "redis-lock";

    String key() default "redis-lock"; // compulsory
    long lockSeconds() default 5; // compulsory
}

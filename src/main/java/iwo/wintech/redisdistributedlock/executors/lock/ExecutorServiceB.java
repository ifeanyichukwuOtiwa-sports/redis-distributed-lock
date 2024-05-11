package iwo.wintech.redisdistributedlock.executors.lock;

import iwo.wintech.redisdistributedlock.lock.aspects.RedisLockAspect;
import iwo.wintech.redisdistributedlock.sample.ProcessOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExecutorServiceB {
    private final ProcessOrder processOrder;


    @Scheduled(cron = "${executor.scheduler.cron}")
    @RedisLockAspect
    public void execute() {
        log.info("Executing ExecutorServiceB");
        processOrder.processOrder("1", "ExecutorServiceB");
    }
}
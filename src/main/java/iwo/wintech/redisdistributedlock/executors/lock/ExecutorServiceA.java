package iwo.wintech.redisdistributedlock.executors.lock;

import iwo.wintech.redisdistributedlock.lock.DistributedLock;
import iwo.wintech.redisdistributedlock.sample.ProcessOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
@Component
public class ExecutorServiceA {
    private final DistributedLock distributedLock;
    private final ProcessOrder processOrder;
    @Scheduled(cron = "${executor.scheduler.cron}")
    public void execute() {
        distributedLock.lockAndExec("redis-lock",
                Duration.ofSeconds(5),
                () -> {
                    log.info("Executing ExecutorServiceA");
                    processOrder.processOrder("1", "ExecutorServiceA");
                });
    }
}

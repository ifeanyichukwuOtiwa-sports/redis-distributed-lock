package iwo.wintech.redisdistributedlock.executors.unlock;

import iwo.wintech.redisdistributedlock.sample.ProcessOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
//@Component
public class ExecutorServiceD {

    private final ProcessOrder processOrder;

    @Scheduled(cron = "${executor.scheduler.cron}")
    public void execute() {
        IntStream.range(0, 3)
                        .parallel()
                                .mapToObj(i -> new Thread(() -> {
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        throw new RuntimeException(e);
                                    }
                                    Thread.currentThread().setName("Thread " + i);
                                    final String name = Thread.currentThread().getName();
                                    log.info("Executing {} Without Lock:", name);
                                    processOrder.processOrder("1", "Non " + name);
                                }))
                                        .forEach(Thread::start);

    }
}

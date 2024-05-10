package iwo.wintech.redisdistributedlock.sample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.Thread;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProcessOrder {
    private static final String PROCESSING = "PROCESSING";
    private static final Map<String, Order> orderMap;
    private static int status = 1;

    private final Clock clock;

    static {
        final Map<String, Order> stringOrderMap = Map.of(
                "1", new Order("1", PROCESSING, null),
                "2", new Order("2", PROCESSING, null),
                "3", new Order("3", PROCESSING, null));

        orderMap = new HashMap<>(stringOrderMap);
    }


    public void processOrder(final String orderId, final String serviceId) {
        status ++;
        final Order order = orderMap.get(orderId);
        if (order == null) {
            return;
        }

        // Simulate processing time (remove this in production)
        try {
            Thread.sleep(5000); // Simulate processing time of 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        order.setOrderTime(LocalDateTime.now());
        order.setStatus("PROCESSED");
        orderMap.put(orderId, order);
        log.info("Processed order state {}: by {}, status: {}", order, serviceId, status);
    }
}

package iwo.wintech.redisdistributedlock.sample;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Order {
    private String id;
    private String status;
    private LocalDateTime orderTime;

}

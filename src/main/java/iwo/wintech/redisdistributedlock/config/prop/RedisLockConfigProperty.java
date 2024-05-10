package iwo.wintech.redisdistributedlock.config.prop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix ="redis-lock")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisLockConfigProperty {
    private String host;
    private Integer port;
    private Long connectionTimeout;
    private Long readTimeout;
}

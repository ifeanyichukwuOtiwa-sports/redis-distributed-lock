package iwo.wintech.redisdistributedlock.config;

import iwo.wintech.redisdistributedlock.config.prop.RedisLockConfigProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Clock;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RedisLockConfigProperty.class)
public class RedisDistributedLockConfig {


    @Bean
    public RedisConnectionFactory redisConnectionFactory(final RedisStandaloneConfiguration standaloneConfig,
                                                         final JedisClientConfiguration clientConfiguration) {
        return new JedisConnectionFactory(standaloneConfig, clientConfiguration);
    }

    @Bean
    public RedisStandaloneConfiguration getRedisStandaloneConfiguration(final RedisLockConfigProperty prop) {
        return new RedisStandaloneConfiguration(prop.getHost(), prop.getPort());
    }

    @Bean
    public JedisClientConfiguration jedisClientConfiguration(final RedisLockConfigProperty prop) {
        return JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofSeconds(prop.getConnectionTimeout()))
                .readTimeout(Duration.ofSeconds(prop.getReadTimeout()))
                .clientName("redis-lock")
                .usePooling()
                .build();
    }

    @Bean
    public RedisTemplate<String, byte[]> redisTemplate(final RedisConnectionFactory factory) {
        final RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.byteArray());
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}

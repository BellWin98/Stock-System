package com.bellwin.stock.infra;

import com.bellwin.stock.config.StockProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;
    private final StockProperties stockProperties;

    public boolean tryAcquire(String key) {
        String redisKey = "rate:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1L) {
            redisTemplate.expire(redisKey, Duration.ofSeconds(stockProperties.getWindowSeconds()));
        }
        return count != null && count <= stockProperties.getMaxRequestsPerSecond();
    }
}

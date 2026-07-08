package id.my.hendisantika.compose.rate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Simple Redis-based sliding-window approximated rate limiter.
 * - per-second window: max 1
 * - per-10-seconds window: max 5 (burst)
 *
 * Uses Redis INCR on time-bucket keys with short TTLs.
 */
@Component
@RequiredArgsConstructor
public class RedisRateLimiterService implements RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean allowRequest(Long userId) {
        if (userId == null) return false;
        long now = Instant.now().getEpochSecond();
        String key1s = "playback:rate:1s:" + userId + ":" + now;
        String key10s = "playback:rate:10s:" + userId + ":" + (now / 10);

        try {
            var v1 = redisTemplate.opsForValue().increment(key1s);
            if (v1 != null && v1 == 1) redisTemplate.expire(key1s, 2, TimeUnit.SECONDS);
            if (v1 != null && v1 > 1) return false; // more than 1 in current second

            var v10 = redisTemplate.opsForValue().increment(key10s);
            if (v10 != null && v10 == 1) redisTemplate.expire(key10s, 11, TimeUnit.SECONDS);
            if (v10 != null && v10 > 5) return false; // burst exceeded in 10s window

            return true;
        } catch (Exception e) {
            // on Redis failure, favor allowing to avoid blocking traffic
            return true;
        }
    }
}

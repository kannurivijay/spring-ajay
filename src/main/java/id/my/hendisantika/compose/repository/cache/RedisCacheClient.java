package id.my.hendisantika.compose.repository.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisCacheClient implements CacheClient {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private String keyFor(Long userId) { return "playback:user:" + userId; }

    private String fieldFor(String mediaId, String device) {
        if (device == null || device.isBlank()) return mediaId;
        return mediaId + ":" + device;
    }

    @Override
    public Optional<PlaybackProgress> get(Long userId, String mediaId, String device) {
        try {
            HashOperations<String, String, String> ops = redisTemplate.opsForHash();
            String json = ops.get(keyFor(userId), fieldFor(mediaId, device));
            if (json == null) return Optional.empty();
            PlaybackProgress p = objectMapper.readValue(json, PlaybackProgress.class);
            return Optional.of(p);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public void put(PlaybackProgress record) {
        try {
            HashOperations<String, String, String> ops = redisTemplate.opsForHash();
            String json = objectMapper.writeValueAsString(record);
            ops.put(keyFor(record.getUserId()), fieldFor(record.getMediaId(), record.getDevice()), json);
        } catch (JsonProcessingException e) {
            // swallow serialization errors for cache write
        }
    }

    @Override
    public Map<String, PlaybackProgress> getAll(Long userId) {
        try {
            HashOperations<String, String, String> ops = redisTemplate.opsForHash();
            Map<String, String> all = ops.entries(keyFor(userId));
            if (all == null || all.isEmpty()) return Map.of();
            return all.entrySet().stream().map(e -> {
                try {
                    return Map.entry(e.getKey(), objectMapper.readValue(e.getValue(), PlaybackProgress.class));
                } catch (JsonProcessingException ex) {
                    return null;
                }
            }).filter(e -> e != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception ex) {
            return Map.of();
        }
    }

    @Override
    public boolean setIdempotencyKey(Long userId, String idempotencyKey, long ttlSeconds) {
        try {
            String key = "playback:idempotency:" + userId + ":" + idempotencyKey;
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            Boolean set = ops.setIfAbsent(key, "1", ttlSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(set);
        } catch (Exception ex) {
            return false;
        }
    }
}

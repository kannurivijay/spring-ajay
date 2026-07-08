package id.my.hendisantika.compose.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheClientTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    HashOperations<String, String, String> hashOps;

    @Mock
    ValueOperations<String, String> valueOps;

    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    RedisCacheClient cacheClient;

    PlaybackProgress sample;

    @BeforeEach
    void setup() {
        cacheClient = new RedisCacheClient(redisTemplate, objectMapper);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        sample = new PlaybackProgress(1L, "m1", "MOBILE", 1000L, 5000L, Instant.now(), Instant.now());
    }

    @Test
    void put_and_get_roundtrip() throws JsonProcessingException {
        String key = "playback:user:1";
        String json = objectMapper.writeValueAsString(sample);
        doNothing().when(hashOps).put(key, "m1:MOBILE", json);
        when(hashOps.get(key, "m1:MOBILE")).thenReturn(json);

        cacheClient.put(sample);
        var opt = cacheClient.get(1L, "m1", "MOBILE");
        assertTrue(opt.isPresent());
        assertEquals("m1", opt.get().getMediaId());
    }

    @Test
    void getAll_returnsMap() throws JsonProcessingException {
        String key = "playback:user:1";
        String json = objectMapper.writeValueAsString(sample);
        when(hashOps.entries(key)).thenReturn(Map.of("m1:MOBILE", json));
        var all = cacheClient.getAll(1L);
        assertEquals(1, all.size());
        assertTrue(all.containsKey("m1:MOBILE"));
    }

    @Test
    void setIdempotencyKey_setsWhenAbsent() {
        String idempKey = "abc";
        String redisKey = "playback:idempotency:1:abc";
        when(valueOps.setIfAbsent(redisKey, "1", 30, java.util.concurrent.TimeUnit.SECONDS)).thenReturn(Boolean.TRUE);
        boolean set = cacheClient.setIdempotencyKey(1L, idempKey, 30);
        assertTrue(set);
    }
}

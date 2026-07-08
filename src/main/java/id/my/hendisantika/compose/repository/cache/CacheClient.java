package id.my.hendisantika.compose.repository.cache;

import id.my.hendisantika.compose.entity.PlaybackProgress;

import java.util.Optional;
import java.util.Map;

public interface CacheClient {
    Optional<PlaybackProgress> get(Long userId, String mediaId, String device);
    void put(PlaybackProgress record);
    Map<String, PlaybackProgress> getAll(Long userId);

    /**
     * Try to set an idempotency key for a user. Returns true if key was set (didn't exist), false if existed.
     */
    boolean setIdempotencyKey(Long userId, String idempotencyKey, long ttlSeconds);
}

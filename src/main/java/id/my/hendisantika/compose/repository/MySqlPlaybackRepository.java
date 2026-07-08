package id.my.hendisantika.compose.repository;

import id.my.hendisantika.compose.entity.PlaybackProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MySQL-backed implementation of PlaybackRepository. Uses JPA for durable store,
 * a cache client for fast reads, and an event publisher for ingestion fan-out.
 */
@Repository
@RequiredArgsConstructor
public class MySqlPlaybackRepository implements PlaybackRepository {

    private final PlaybackProgressJpaRepository jpaRepository;
    private final id.my.hendisantika.compose.repository.cache.CacheClient cacheClient;
    private final id.my.hendisantika.compose.repository.event.EventPublisher eventPublisher;

    @Override
    public Optional<PlaybackProgress> getFromCache(Long userId, String mediaId, String device) {
        if (userId == null || mediaId == null) return Optional.empty();
        return cacheClient.get(userId, mediaId, device);
    }

    @Override
    public void writeToCache(PlaybackProgress record) {
        if (record == null) return;
        cacheClient.put(record);
    }

    @Override
    public void publishEvent(PlaybackProgress record) {
        if (record == null) return;
        eventPublisher.publish(record);
    }

    @Override
    public Optional<PlaybackProgress> getFromStore(Long userId, String mediaId, String device) {
        if (userId == null || mediaId == null) return Optional.empty();
        return jpaRepository.findByUserIdAndMediaIdAndDevice(userId, mediaId, device);
    }

    @Override
    public PlaybackProgress writeToStore(PlaybackProgress record) {
        if (record == null) throw new IllegalArgumentException("record is required");
        return jpaRepository.save(record);
    }

    @Override
    public Page<PlaybackProgress> getByUser(Long userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
    }
}

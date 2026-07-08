package id.my.hendisantika.compose.repository;

import id.my.hendisantika.compose.entity.PlaybackProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Interface that abstracts cache, event publish and persistent store operations for playback records.
 */
public interface PlaybackRepository {
    Optional<PlaybackProgress> getFromCache(Long userId, String mediaId);
    void writeToCache(PlaybackProgress record);
    void publishEvent(PlaybackProgress record);

    Optional<PlaybackProgress> getFromStore(Long userId, String mediaId);
    PlaybackProgress writeToStore(PlaybackProgress record);
    Page<PlaybackProgress> getByUser(Long userId, Pageable pageable);
}

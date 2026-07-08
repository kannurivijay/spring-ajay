package id.my.hendisantika.compose.repository;

import id.my.hendisantika.compose.entity.PlaybackProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PlaybackStoreRepository {
    Optional<PlaybackProgress> getFromStore(Long userId, String mediaId);
    PlaybackProgress writeToStore(PlaybackProgress record);
    Page<PlaybackProgress> getByUser(Long userId, Pageable pageable);
}

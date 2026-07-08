package id.my.hendisantika.compose.repository;

import id.my.hendisantika.compose.entity.PlaybackProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaPlaybackStoreRepository implements PlaybackStoreRepository {

    private final PlaybackProgressJpaRepository jpaRepository;

    @Override
    public Optional<PlaybackProgress> getFromStore(Long userId, String mediaId, String device) {
        return jpaRepository.findByUserIdAndMediaIdAndDevice(userId, mediaId, device);
    }

    @Override
    public PlaybackProgress writeToStore(PlaybackProgress record) {
        return jpaRepository.save(record);
    }

    @Override
    public Page<PlaybackProgress> getByUser(Long userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
    }
}

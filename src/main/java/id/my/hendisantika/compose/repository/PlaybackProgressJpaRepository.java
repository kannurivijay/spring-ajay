package id.my.hendisantika.compose.repository;

import id.my.hendisantika.compose.entity.PlaybackProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlaybackProgressJpaRepository extends JpaRepository<PlaybackProgress, Long> {
    Optional<PlaybackProgress> findByUserIdAndMediaIdAndDevice(Long userId, String mediaId, String device);
    Page<PlaybackProgress> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);
}

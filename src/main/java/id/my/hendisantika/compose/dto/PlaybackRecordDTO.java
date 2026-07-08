package id.my.hendisantika.compose.dto;

import java.time.Instant;

public record PlaybackRecordDTO(Long id, Long userId, String mediaId, Long positionMs, Long durationMs, Instant createdAt, Instant updatedAt) {
}

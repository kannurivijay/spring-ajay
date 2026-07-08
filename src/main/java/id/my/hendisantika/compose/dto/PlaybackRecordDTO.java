package id.my.hendisantika.compose.dto;

import java.time.Instant;

public record PlaybackRecordDTO(Long id, Long userId, String mediaId, String device, Long positionMs, Long durationMs, Instant createdAt, Instant updatedAt) {
}

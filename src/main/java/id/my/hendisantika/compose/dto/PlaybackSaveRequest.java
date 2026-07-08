package id.my.hendisantika.compose.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class PlaybackSaveRequest {

    @NotNull
    private Long userId;

    @NotNull
    @Size(min = 1)
    private String mediaId;

    @Min(0)
    private Long positionMs;

    @Min(0)
    private Long durationMs;

    private String device;

    private Instant updatedAt;

    public PlaybackSaveRequest() {}

    public PlaybackSaveRequest(Long userId, String mediaId, Long positionMs, Long durationMs, Instant updatedAt, String device) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.positionMs = positionMs;
        this.durationMs = durationMs;
        this.updatedAt = updatedAt;
        this.device = device;
    }

    public Long getUserId() { return userId; }
    public String getMediaId() { return mediaId; }
    public Long getPositionMs() { return positionMs; }
    public Long getDurationMs() { return durationMs; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getDevice() { return device; }
}

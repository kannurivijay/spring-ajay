package id.my.hendisantika.compose.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "playback_progress", indexes = {@Index(name = "idx_user_media_device", columnList = "user_id,media_id,device")})
public class PlaybackProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "media_id", nullable = false, length = 200)
    private String mediaId;

    @Column(name = "device", length = 50)
    private String device;

    @Column(name = "position_ms")
    private Long positionMs;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public PlaybackProgress() {}

    public PlaybackProgress(Long userId, String mediaId, String device, Long positionMs, Long durationMs, Instant createdAt, Instant updatedAt) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.device = device;
        this.positionMs = positionMs;
        this.durationMs = durationMs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getMediaId() { return mediaId; }
    public String getDevice() { return device; }
    public Long getPositionMs() { return positionMs; }
    public Long getDurationMs() { return durationMs; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    public void setPositionMs(Long positionMs) { this.positionMs = positionMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

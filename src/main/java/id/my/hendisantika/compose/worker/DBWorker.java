package id.my.hendisantika.compose.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.config.PlaybackProperties;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import id.my.hendisantika.compose.repository.event.KafkaProducerClient;
import id.my.hendisantika.compose.repository.PlaybackProgressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DBWorker consumes playback events, batches them, merges per userId+mediaId and persists
 * to MySQL with conditional update: only write when incoming.updatedAt > existing.updatedAt.
 * Failed records after retries are published to a DLQ topic.
 */
@Component
@RequiredArgsConstructor
public class DBWorker {

    private final ObjectMapper objectMapper;
    private final PlaybackProgressJpaRepository jpaRepository;
    private final KafkaProducerClient producerClient;
    private final PlaybackProperties properties;

    private final ConcurrentHashMap<String, PlaybackProgress> buffer = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "dbworker-flusher"));
        scheduler.scheduleAtFixedRate(this::flushBufferSafely, properties.getWorkerFlushMs(), properties.getWorkerFlushMs(), TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() {
        if (scheduler != null) scheduler.shutdown();
        flushBufferSafely();
    }

    @KafkaListener(topics = "${playback.topic}", groupId = "playback-dbworker")
    public void onMessage(String payload) {
        try {
            PlaybackProgress incoming = objectMapper.readValue(payload, PlaybackProgress.class);
            String key = keyFor(incoming.getUserId(), incoming.getMediaId());
            buffer.merge(key, incoming, (a, b) -> a.getUpdatedAt().isAfter(b.getUpdatedAt()) ? a : b);
            if (buffer.size() >= properties.getWorkerBatchSize()) {
                flushBufferSafely();
            }
        } catch (Exception e) {
            // failed to deserialize — publish to DLQ directly
            try { producerClient.publishToTopic(properties.getDlqTopic(), "", payload); } catch (Exception ex) {}
        }
    }

    // Exposed for tests to inject messages programmatically
    public void acceptPayload(String payload) { onMessage(payload); }

    private String keyFor(Long userId, String mediaId) { return userId + "#" + mediaId; }

    private void flushBufferSafely() {
        try {
            flushBuffer();
        } catch (Exception e) {
            // log and swallow — scheduler will retry on next run
        }
    }

    public void flushBuffer() {
        List<PlaybackProgress> toPersist = new ArrayList<>();
        synchronized (buffer) {
            if (buffer.isEmpty()) return;
            for (Map.Entry<String, PlaybackProgress> e : buffer.entrySet()) {
                toPersist.add(e.getValue());
            }
            buffer.clear();
        }
        if (!toPersist.isEmpty()) persistBatch(toPersist);
    }

    private void persistBatch(Collection<PlaybackProgress> items) {
        for (PlaybackProgress incoming : items) {
            persistWithRetries(incoming);
        }
    }

    @Transactional
    protected void persistWithRetries(PlaybackProgress incoming) {
        int maxAttempts = Math.max(1, properties.getWorkerMaxRetries());
        AtomicInteger attempt = new AtomicInteger(0);
        while (attempt.incrementAndGet() <= maxAttempts) {
            try {
                Optional<PlaybackProgress> existingOpt = jpaRepository.findByUserIdAndMediaIdAndDevice(incoming.getUserId(), incoming.getMediaId(), incoming.getDevice());
                if (existingOpt.isEmpty()) {
                    // new record
                    PlaybackProgress toSave = new PlaybackProgress(incoming.getUserId(), incoming.getMediaId(), incoming.getDevice(), incoming.getPositionMs(), incoming.getDurationMs(), Instant.now(), incoming.getUpdatedAt());
                    jpaRepository.save(toSave);
                } else {
                    PlaybackProgress existing = existingOpt.get();
                    if (incoming.getUpdatedAt().isAfter(existing.getUpdatedAt())) {
                        existing.setPositionMs(incoming.getPositionMs());
                        existing.setDurationMs(incoming.getDurationMs());
                        existing.setUpdatedAt(incoming.getUpdatedAt());
                        jpaRepository.save(existing);
                    }
                }
                return; // success
            } catch (Exception e) {
                if (attempt.get() >= maxAttempts) {
                    // final failure — publish to DLQ
                    try {
                        String payload;
                        try { payload = objectMapper.writeValueAsString(incoming); } catch (Exception ex) { payload = ""; }
                        producerClient.publishToTopic(properties.getDlqTopic(), String.valueOf(incoming.getUserId()), payload);
                    } catch (Exception ex) {
                        // swallow — nothing more we can do here
                    }
                    return;
                }
                try { Thread.sleep(100L * attempt.get()); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
    }
}

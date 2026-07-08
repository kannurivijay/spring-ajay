package id.my.hendisantika.compose.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.config.PlaybackProperties;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import id.my.hendisantika.compose.repository.event.KafkaProducerClient;
import id.my.hendisantika.compose.repository.PlaybackProgressJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBWorkerTest {

    @Mock
    ObjectMapper objectMapper;

    @Mock
    PlaybackProgressJpaRepository jpaRepository;

    @Mock
    KafkaProducerClient producerClient;

    PlaybackProperties properties;

    @InjectMocks
    DBWorker worker;

    PlaybackProgress incoming;

    @BeforeEach
    void setup() throws Exception {
        properties = new PlaybackProperties();
        properties.setWorkerBatchSize(10);
        properties.setWorkerMaxRetries(2);
        // inject via reflection by recreating worker
        this.worker = new DBWorker(new ObjectMapper(), jpaRepository, producerClient, properties);
        incoming = new PlaybackProgress(1L, "m1", 1000L, 5000L, Instant.now(), Instant.now());
    }

    @Test
    void persist_newRecord_saved() throws Exception {
        String payload = new ObjectMapper().writeValueAsString(incoming);
        // simulate no existing
        when(jpaRepository.findByUserIdAndMediaId(1L, "m1")).thenReturn(Optional.empty());

        worker.acceptPayload(payload);
        worker.flushBuffer();

        verify(jpaRepository, atLeastOnce()).save(any(PlaybackProgress.class));
    }

    @Test
    void persist_olderIncoming_skipped() throws Exception {
        Instant now = Instant.now();
        PlaybackProgress existing = new PlaybackProgress(1L, "m1", 2000L, 5000L, now.minusSeconds(10), now.plusSeconds(100));
        PlaybackProgress olderIncoming = new PlaybackProgress(1L, "m1", 1000L, 5000L, now.minusSeconds(20), now);
        String payload = new ObjectMapper().writeValueAsString(olderIncoming);
        when(jpaRepository.findByUserIdAndMediaId(1L, "m1")).thenReturn(Optional.of(existing));

        worker.acceptPayload(payload);
        worker.flushBuffer();

        verify(jpaRepository, never()).save(existing);
    }

    @Test
    void persist_failure_publishesToDlq() throws Exception {
        String payload = new ObjectMapper().writeValueAsString(incoming);
        when(jpaRepository.findByUserIdAndMediaId(1L, "m1")).thenThrow(new RuntimeException("dbdown"));

        worker.acceptPayload(payload);
        worker.flushBuffer();

        verify(producerClient, atLeastOnce()).publishToTopic(eq(properties.getDlqTopic()), anyString(), anyString());
    }
}

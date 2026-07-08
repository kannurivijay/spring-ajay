package id.my.hendisantika.compose.repository;

import id.my.hendisantika.compose.config.PlaybackProperties;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerClientTest {

    @Mock
    org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    PlaybackProperties props;

    SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @InjectMocks
    KafkaProducerClient producerClient;

    @BeforeEach
    void setup() {
        props = new PlaybackProperties();
        props.setTopic("playback-progress.v1");
        props.setPublishRetries(3);
        props.setPublishTimeoutMs(200);
        // inject props via reflection
        this.producerClient = new KafkaProducerClient(kafkaTemplate, props, meterRegistry);
    }

    @Test
    void publish_success_noRetry() throws Exception {
        SettableListenableFuture<Object> future = new SettableListenableFuture<>();
        future.set(new Object());
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        assertDoesNotThrow(() -> producerClient.publish("1", "payload"));
        verify(kafkaTemplate, times(1)).send("playback-progress.v1", "1", "payload");
    }

    @Test
    void publish_retriesAndFails_throws() throws Exception {
        SettableListenableFuture<Object> f1 = new SettableListenableFuture<>();
        f1.setException(new RuntimeException("boom1"));
        SettableListenableFuture<Object> f2 = new SettableListenableFuture<>();
        f2.setException(new RuntimeException("boom2"));
        SettableListenableFuture<Object> f3 = new SettableListenableFuture<>();
        f3.setException(new RuntimeException("boom3"));

        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(f1, f2, f3);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> producerClient.publish("1", "payload"));
        assertTrue(ex.getMessage().contains("Failed to publish"));
        verify(kafkaTemplate, times(3)).send("playback-progress.v1", "1", "payload");
    }
}

package id.my.hendisantika.compose.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    KafkaProducerClient producerClient;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    KafkaEventPublisher publisher;

    PlaybackProgress sample;

    @BeforeEach
    void setup() {
        sample = new PlaybackProgress(1L, "m1", "MOBILE", 1000L, 5000L, Instant.now(), Instant.now());
    }

    @Test
    void publish_serializesAndDelegates() throws Exception {
        when(objectMapper.writeValueAsString(sample)).thenReturn("{json}");
        publisher.publish(sample);
        verify(producerClient).publish("1", "{json}");
    }
}

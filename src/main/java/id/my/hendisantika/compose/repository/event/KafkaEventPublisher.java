package id.my.hendisantika.compose.repository.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaProducerClient producerClient;
    private final ObjectMapper objectMapper;
    private final org.springframework.core.env.Environment env;

    @Override
    public void publish(PlaybackProgress record) {
        try {
            String topic = env.getProperty("playback.topic", "playback-progress.v1");
            String payload = objectMapper.writeValueAsString(record);
            producerClient.publishToTopic(topic, String.valueOf(record.getUserId()), payload);
        } catch (JsonProcessingException e) {
            // swallow
        }
    }
}

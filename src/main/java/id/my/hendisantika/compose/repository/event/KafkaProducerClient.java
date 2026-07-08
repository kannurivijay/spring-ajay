package id.my.hendisantika.compose.repository.event;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducerClient {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MeterRegistry meterRegistry;

    public void publishToTopic(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload);
        meterRegistry.counter("playback.producer.success").increment();
    }
}

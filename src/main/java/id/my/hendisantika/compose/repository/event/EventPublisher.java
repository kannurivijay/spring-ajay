package id.my.hendisantika.compose.repository.event;

import id.my.hendisantika.compose.entity.PlaybackProgress;

public interface EventPublisher {
    void publish(PlaybackProgress record);
}

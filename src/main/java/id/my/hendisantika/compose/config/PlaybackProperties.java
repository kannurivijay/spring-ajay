package id.my.hendisantika.compose.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "playback")
public class PlaybackProperties {
    /** Kafka topic or other transport topic name. */
    private String topic = "playback-progress.v1";

    /** Number of publish retries. */
    private int publishRetries = 3;

    /** Timeout for publish future in milliseconds. */
    private long publishTimeoutMs = 5000;
    private String dlqTopic = "playback-progress-dlq.v1";
    private int workerBatchSize = 100;
    private long workerFlushMs = 5000;
    private int workerMaxRetries = 3;

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public int getPublishRetries() { return publishRetries; }
    public void setPublishRetries(int publishRetries) { this.publishRetries = publishRetries; }
    public long getPublishTimeoutMs() { return publishTimeoutMs; }
    public void setPublishTimeoutMs(long publishTimeoutMs) { this.publishTimeoutMs = publishTimeoutMs; }
    public String getDlqTopic() { return dlqTopic; }
    public void setDlqTopic(String dlqTopic) { this.dlqTopic = dlqTopic; }
    public int getWorkerBatchSize() { return workerBatchSize; }
    public void setWorkerBatchSize(int workerBatchSize) { this.workerBatchSize = workerBatchSize; }
    public long getWorkerFlushMs() { return workerFlushMs; }
    public void setWorkerFlushMs(long workerFlushMs) { this.workerFlushMs = workerFlushMs; }
    public int getWorkerMaxRetries() { return workerMaxRetries; }
    public void setWorkerMaxRetries(int workerMaxRetries) { this.workerMaxRetries = workerMaxRetries; }
}

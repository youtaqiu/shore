package run.vexa.reactor.kafka.properties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shore.kafka")
public class KafkaProperties {

    private boolean enabled = true;
    private String bootstrapServers = "localhost:9092";
    private String clientId = "shore-kafka";

    // sender
    private String acks = "all";
    private Duration linger = Duration.ofMillis(5);
    private int batchSize = 16_384; // 16KB
    private long bufferMemory = 33_554_432; // 32MB
    private int maxInFlight = 1024;

    // receiver
    private String groupId = "shore-group";
    private String autoOffsetReset = "latest";
    private boolean enableAutoCommit = true;
    private int maxPollRecords = 500;
    private Duration sessionTimeout = Duration.ofSeconds(10);
    private Duration requestTimeout = Duration.ofSeconds(30);
    private List<String> topics = new ArrayList<>();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getAcks() { return acks; }
    public void setAcks(String acks) { this.acks = acks; }

    public Duration getLinger() { return linger; }
    public void setLinger(Duration linger) { this.linger = linger; }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    public long getBufferMemory() { return bufferMemory; }
    public void setBufferMemory(long bufferMemory) { this.bufferMemory = bufferMemory; }

    public int getMaxInFlight() { return maxInFlight; }
    public void setMaxInFlight(int maxInFlight) { this.maxInFlight = maxInFlight; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getAutoOffsetReset() { return autoOffsetReset; }
    public void setAutoOffsetReset(String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; }

    public boolean isEnableAutoCommit() { return enableAutoCommit; }
    public void setEnableAutoCommit(boolean enableAutoCommit) { this.enableAutoCommit = enableAutoCommit; }

    public int getMaxPollRecords() { return maxPollRecords; }
    public void setMaxPollRecords(int maxPollRecords) { this.maxPollRecords = maxPollRecords; }

    public Duration getSessionTimeout() { return sessionTimeout; }
    public void setSessionTimeout(Duration sessionTimeout) { this.sessionTimeout = sessionTimeout; }

    public Duration getRequestTimeout() { return requestTimeout; }
    public void setRequestTimeout(Duration requestTimeout) { this.requestTimeout = requestTimeout; }

    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }
}



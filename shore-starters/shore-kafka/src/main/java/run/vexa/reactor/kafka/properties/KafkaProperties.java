package run.vexa.reactor.kafka.properties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Configuration properties for the Shore Kafka starter.
 * <p>
 * Binds properties with prefix {@code shore.kafka} to configure producer and consumer
 * settings for Reactor Kafka.
 */
@ConfigurationProperties(prefix = "shore.kafka")
@Data
public class KafkaProperties {

    /** Whether the starter is enabled. */
    private boolean enabled = true;

    /** Comma-separated Kafka bootstrap servers. */
    private String bootstrapServers = "localhost:9092";

    /** Kafka client id for the producer. */
    private String clientId = "shore-kafka";

    // sender
    /** Producer acks strategy, e.g. all, 1, 0. */
    private String acks = "all";

    /** Producer linger duration. */
    private Duration linger = Duration.ofMillis(5);

    /** Producer batch size in bytes. */
    private int batchSize = 16_384; // 16KB

    /** Producer buffer memory in bytes. */
    private long bufferMemory = 33_554_432; // 32MB

    /** Max in-flight requests per connection. */
    private int maxInFlight = 1024;

    // receiver
    /** Default consumer group id. */
    private String groupId = "shore-group";

    /** Offset reset behavior, e.g. earliest or latest. */
    private String autoOffsetReset = "latest";

    /** Whether to enable auto-commit for consumers. */
    private boolean enableAutoCommit = true;

    /** Max records returned in a single poll. */
    private int maxPollRecords = 500;

    /** Consumer session timeout. */
    private Duration sessionTimeout = Duration.ofSeconds(10);

    /** Consumer request timeout. */
    private Duration requestTimeout = Duration.ofSeconds(30);

    /** Default topics to subscribe if provided. */
    private List<String> topics = new ArrayList<>();
}



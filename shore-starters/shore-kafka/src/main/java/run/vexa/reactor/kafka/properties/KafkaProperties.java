package run.vexa.reactor.kafka.properties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "shore.kafka")
@Data
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
}



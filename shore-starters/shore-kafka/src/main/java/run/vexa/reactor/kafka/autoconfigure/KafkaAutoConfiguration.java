package run.vexa.reactor.kafka.autoconfigure;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import run.vexa.reactor.kafka.properties.KafkaProperties;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnProperty(prefix = "shore.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SenderOptions<String, byte[]> kafkaSenderOptions(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        config.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getClientId());
        config.put(ProducerConfig.ACKS_CONFIG, properties.getAcks());
        config.put(ProducerConfig.LINGER_MS_CONFIG, properties.getLinger().toMillis());
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.getBatchSize());
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, properties.getBufferMemory());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        return SenderOptions.<String, byte[]>create(config)
            .maxInFlight(properties.getMaxInFlight());
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaSender<String, byte[]> kafkaSender(SenderOptions<String, byte[]> senderOptions) {
        return KafkaSender.create(senderOptions);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReceiverOptions<String, byte[]> kafkaReceiverOptions(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.isEnableAutoCommit());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.getMaxPollRecords());
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, (int) properties.getSessionTimeout().toMillis());
        config.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, (int) properties.getRequestTimeout().toMillis());

        ReceiverOptions<String, byte[]> options = ReceiverOptions.create(config);
        if (!properties.getTopics().isEmpty()) {
            options = options.subscription(properties.getTopics());
        }
        return options;
    }
}



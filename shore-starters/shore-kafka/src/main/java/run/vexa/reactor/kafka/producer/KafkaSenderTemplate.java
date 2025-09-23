package run.vexa.reactor.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.nio.charset.StandardCharsets;

/**
 * Simple wrapper around {@link KafkaSender} to send records using Reactor.
 */
public class KafkaSenderTemplate {

    private final KafkaSender<String, byte[]> sender;

    /**
     * Construct a template with the given {@link KafkaSender}.
     * @param sender sender
     */
    public KafkaSenderTemplate(KafkaSender<String, byte[]> sender) {
        this.sender = sender;
    }

    /**
     * Send a UTF-8 string payload.
     * @param topic topic
     * @param key key
     * @param value value
     * @return voidMono
     */
    public Mono<Void> send(String topic, String key, String value) {
        byte[] bytes = value == null ? null : value.getBytes(StandardCharsets.UTF_8);
        return send(topic, key, bytes);
    }

    /**
     * Send a raw byte array payload.
     * @param topic topic
     * @param key key
     * @param value value
     * @return voidMono
     */
    public Mono<Void> send(String topic, String key, byte[] value) {
        SenderRecord<String, byte[], Void> record = SenderRecord.create(new ProducerRecord<>(topic, key, value), null);
        return sender.send(Mono.just(record)).then();
    }
}



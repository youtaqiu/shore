package run.vexa.reactor.kafka.producer;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;

import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

public class KafkaSenderTemplate {

    private final KafkaSender<String, byte[]> sender;

    public KafkaSenderTemplate(KafkaSender<String, byte[]> sender) {
        this.sender = sender;
    }

    public Mono<Void> send(String topic, String key, String value) {
        byte[] bytes = value == null ? null : value.getBytes(StandardCharsets.UTF_8);
        return send(topic, key, bytes);
    }

    public Mono<Void> send(String topic, String key, byte[] value) {
        SenderRecord<String, byte[], Void> record = SenderRecord.create(new ProducerRecord<>(topic, key, value), null);
        return sender.send(Mono.just(record)).then();
    }
}



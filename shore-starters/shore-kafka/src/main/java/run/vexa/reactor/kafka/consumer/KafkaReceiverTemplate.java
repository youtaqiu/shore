package run.vexa.reactor.kafka.consumer;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

public class KafkaReceiverTemplate {

    private final KafkaReceiver<String, byte[]> receiver;

    public KafkaReceiverTemplate(ReceiverOptions<String, byte[]> options) {
        this.receiver = KafkaReceiver.create(options);
    }

    public Flux<ConsumerRecord<String, byte[]>> receiveRaw() {
        return receiver.receiveAutoAck().concatMap(r -> r);
    }

    public Flux<String> receiveAsString() {
        return receiveRaw().map(rec -> rec.value() == null ? null : new String(rec.value(), StandardCharsets.UTF_8));
    }
}



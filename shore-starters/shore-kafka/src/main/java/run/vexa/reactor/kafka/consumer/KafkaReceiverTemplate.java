package run.vexa.reactor.kafka.consumer;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

/**
 * Simple wrapper around {@link KafkaReceiver} to consume records using Reactor.
 */
public class KafkaReceiverTemplate {

    private final KafkaReceiver<String, byte[]> receiver;

    /**
     * Construct a template with the given {@link ReceiverOptions}.
     */
    public KafkaReceiverTemplate(ReceiverOptions<String, byte[]> options) {
        this.receiver = KafkaReceiver.create(options);
    }

    /**
     * Receive raw {@link ConsumerRecord} stream.
     */
    public Flux<ConsumerRecord<String, byte[]>> receiveRaw() {
        return receiver.receiveAutoAck().concatMap(r -> r);
    }

    /**
     * Receive string payloads decoded as UTF-8.
     */
    public Flux<String> receiveAsString() {
        return receiveRaw().map(rec -> rec.value() == null ? null : new String(rec.value(), StandardCharsets.UTF_8));
    }
}



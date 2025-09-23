package run.vexa.reactor.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.nio.charset.StandardCharsets;

/**
 * Simple wrapper around {@link KafkaReceiver} to consume records using Reactor.
 */
public class KafkaReceiverTemplate {

    private final KafkaReceiver<String, byte[]> receiver;

    /**
     * Construct a template with the given {@link ReceiverOptions}.
     * @param options options
     */
    public KafkaReceiverTemplate(ReceiverOptions<String, byte[]> options) {
        this.receiver = KafkaReceiver.create(options);
    }

    /**
     * Receive raw {@link ConsumerRecord} stream.
     * @return consumerRecordFlux
     */
    public Flux<ConsumerRecord<String, byte[]>> receiveRaw() {
        return receiver.receiveAutoAck().concatMap(r -> r);
    }

    /**
     * Receive string payloads decoded as UTF-8.
     * @return messageFlux
     */
    public Flux<String> receiveAsString() {
        return receiveRaw().mapNotNull(rec -> rec.value() == null ? null : new String(rec.value(), StandardCharsets.UTF_8));
    }
}



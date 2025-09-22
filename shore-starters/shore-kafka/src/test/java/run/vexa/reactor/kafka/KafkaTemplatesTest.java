package run.vexa.reactor.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import run.vexa.reactor.kafka.consumer.KafkaReceiverTemplate;
import run.vexa.reactor.kafka.producer.KafkaSenderTemplate;

class KafkaTemplatesTest {

    @Test
    void senderTemplateSendsString() {
        @SuppressWarnings("unchecked")
        KafkaSender<String, byte[]> sender = (KafkaSender<String, byte[]>) mock(KafkaSender.class);
        when(sender.send(ArgumentMatchers.<Publisher<? extends SenderRecord<String, byte[], Void>>>any()))
                .thenReturn(Flux.empty());
        KafkaSenderTemplate template = new KafkaSenderTemplate(sender);
        Mono<Void> result = template.send("t", "k", "v");
        assertThat(result).isNotNull();
    }

    @Test
    void receiverTemplateMapsToString() {
        byte[] payload = "hello".getBytes(StandardCharsets.UTF_8);
        ConsumerRecord<String, byte[]> rec = new ConsumerRecord<>("t", 0, 0L, "k", payload);
        @SuppressWarnings("unchecked")
        ReceiverOptions<String, byte[]> options = (ReceiverOptions<String, byte[]>) mock(ReceiverOptions.class);
        KafkaReceiverTemplate template = new KafkaReceiverTemplate(options) {
            @Override
            public Flux<ConsumerRecord<String, byte[]>> receiveRaw() {
                return Flux.just(rec);
            }
        };
        List<String> out = template.receiveAsString().collectList().block();
        assertThat(out).containsExactly("hello");
    }
}



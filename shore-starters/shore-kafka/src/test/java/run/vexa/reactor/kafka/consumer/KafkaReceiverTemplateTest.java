package run.vexa.reactor.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaReceiverTemplateTest {

    @Mock
    private KafkaReceiver<String, byte[]> mockReceiver;
    
    @Mock
    private ReceiverOptions<String, byte[]> mockOptions;
    
    @InjectMocks
    private KafkaReceiverTemplate template;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        template = new KafkaReceiverTemplate(mockOptions);
        // Inject the mock receiver
        ReflectionTestUtils.setField(template, "receiver", mockReceiver);
    }

    @Test
    void receiveRaw_ShouldReturnFluxOfConsumerRecords() {
        // Given
        String topic = "test-topic";
        String key = "key1";
        byte[] value = "test-message".getBytes(StandardCharsets.UTF_8);
        
        // Create a test record
        ConsumerRecord<String, byte[]> consumerRecord = new ConsumerRecord<>(topic, 0, 1L, key, value);
        ReceiverOffset offset = Mockito.mock(ReceiverOffset.class);
        ReceiverRecord<String, byte[]> receiverRecord = new ReceiverRecord<>(consumerRecord, offset);
        
        // Mock the receiver to return our test record
        when(mockReceiver.receiveAutoAck())
                .thenReturn(Flux.just(Flux.just(receiverRecord)));
        
        // When & Then
        StepVerifier.create(template.receiveRaw())
                .expectNextMatches(r -> 
                    r.topic().equals(topic) && 
                    r.key().equals(key) &&
                    new String(r.value(), StandardCharsets.UTF_8).equals("test-message")
                )
                .verifyComplete();
    }

    @Test
    void receiveRaw_ShouldHandleEmptyFlux() {
        // Given
        when(mockReceiver.receiveAutoAck())
                .thenReturn(Flux.just(Flux.empty()));
        
        // When & Then
        StepVerifier.create(template.receiveRaw())
                .expectNextCount(0)
                .verifyComplete();
    }
}

package run.vexa.reactor.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;
import reactor.test.StepVerifier;
import run.vexa.reactor.rabbitmq.exception.QueueException;
import run.vexa.reactor.rabbitmq.message.QueueEvent;
import run.vexa.reactor.rabbitmq.properties.RabbitMQProperties;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RabbitMQReceiverTest {

    private Sender sender;
    private Receiver reactorReceiver;
    private ObjectMapper objectMapper;
    private RabbitMQProperties rabbitMQProperties;
    private TestRabbitMQReceiver testReceiver;

    @BeforeEach
    void setUp() throws Exception {
        sender = mock(Sender.class);
        reactorReceiver = mock(Receiver.class);
        objectMapper = new ObjectMapper();
        rabbitMQProperties = new RabbitMQProperties();
        rabbitMQProperties.setRetry(1L);
        rabbitMQProperties.setMinBackoff(0L);

        testReceiver = new TestRabbitMQReceiver();
        ReflectionTestUtils.setField(testReceiver, "sender", sender);
        ReflectionTestUtils.setField(testReceiver, "receiver", reactorReceiver);
        ReflectionTestUtils.setField(testReceiver, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(testReceiver, "rabbitMQProperties", rabbitMQProperties);
    }

    @Test
    void consumeShouldDeserializeAndHandleMessage() throws Exception {
        TestQueueEvent payload = new TestQueueEvent("test-queue", "ok");
        Delivery delivery = delivery(payload);

        when(sender.declare(any(QueueSpecification.class))).thenReturn(Mono.empty());
        when(reactorReceiver.consumeAutoAck("test-queue")).thenReturn(Flux.just(delivery));

        StepVerifier.create(testReceiver.consume("test-queue"))
                .verifyComplete();

        assertThat(testReceiver.handledEvents)
                .singleElement()
                .extracting(TestQueueEvent::getContent)
                .isEqualTo("ok");
        verify(sender).declare(any(QueueSpecification.class));
    }

    @Test
    void consumeShouldRetryWhenHandlerFails() throws Exception {
        rabbitMQProperties.setRetry(2L);
        TestQueueEvent payload = new TestQueueEvent("test-queue", "retry-me");
        Delivery delivery = delivery(payload);

        when(sender.declare(any(QueueSpecification.class))).thenReturn(Mono.empty());
        when(reactorReceiver.consumeAutoAck("test-queue")).thenReturn(Flux.just(delivery));
        testReceiver.failFirst.set(true);

        StepVerifier.create(testReceiver.consume("test-queue"))
                .verifyComplete();

        assertThat(testReceiver.invocationCount.get()).isEqualTo(2);
    }

    @Test
    void consumeShouldWrapJsonParseException() {
        Delivery delivery = new Delivery(new Envelope(1L, false, "", ""), new AMQP.BasicProperties(), "not-json".getBytes());

        when(sender.declare(any(QueueSpecification.class))).thenReturn(Mono.empty());
        when(reactorReceiver.consumeAutoAck("test-queue")).thenReturn(Flux.just(delivery));

        StepVerifier.create(testReceiver.consume("test-queue"))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .hasCauseInstanceOf(QueueException.class);
                    assertThat(throwable.getCause())
                            .hasCauseInstanceOf(com.fasterxml.jackson.core.JsonParseException.class);
                })
                .verify();

        assertThat(testReceiver.invocationCount.get()).isZero();
    }

    private Delivery delivery(TestQueueEvent payload) throws Exception {
        byte[] body = objectMapper.writeValueAsBytes(payload);
        return new Delivery(new Envelope(1L, false, "", ""), new AMQP.BasicProperties(), body);
    }

    private static final class TestQueueEvent extends QueueEvent {

        private String queueName;
        private String content;

        private TestQueueEvent() {
            // for Jackson
        }

        private TestQueueEvent(String queueName, String content) {
            this.queueName = queueName;
            this.content = content;
            setQueue(queueName);
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
            setQueue(queueName);
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String getQueue() {
            return getQueueName();
        }
    }

    private static final class TestRabbitMQReceiver extends RabbitMQReceiver<TestQueueEvent> {
        private final List<TestQueueEvent> handledEvents = new CopyOnWriteArrayList<>();
        private final AtomicInteger invocationCount = new AtomicInteger();
        private final AtomicBoolean failFirst = new AtomicBoolean();

        @Override
        public Mono<Void> handle(TestQueueEvent event) {
            handledEvents.add(event);
            int attempt = invocationCount.incrementAndGet();
            if (failFirst.get() && attempt == 1) {
                return Mono.error(new IllegalStateException("fail"));
            }
            return Mono.empty();
        }

        @Override
        public String getQueue() {
            return "test-queue";
        }
    }
}

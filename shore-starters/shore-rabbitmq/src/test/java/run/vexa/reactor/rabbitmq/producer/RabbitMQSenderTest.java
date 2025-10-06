package run.vexa.reactor.rabbitmq.producer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;
import run.vexa.reactor.rabbitmq.message.QueueEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RabbitMQSenderTest {

    private Sender sender;
    private RabbitMQSender rabbitMQSender;

    @BeforeEach
    void setUp() {
        sender = mock(Sender.class);
        rabbitMQSender = new RabbitMQSender(sender);
    }

    @Test
    void sendShouldPublishWithProvidedRoutingKey() throws InterruptedException {
        TestQueueEvent event = new TestQueueEvent("order.queue", "order.created", "order.exchange");
        List<OutboundMessage> capturedMessages = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        when(sender.declare(any(ExchangeSpecification.class))).thenReturn(Mono.empty());
        when(sender.bind(any(BindingSpecification.class))).thenReturn(Mono.empty());
        when(sender.sendWithPublishConfirms(any())).thenAnswer(invocation -> {
            Flux<OutboundMessage> outboundFlux = invocation.getArgument(0);
            return outboundFlux
                    .doOnNext(capturedMessages::add)
                    .map(message -> new OutboundMessageResult<>(message, true))
                    .doOnNext(result -> latch.countDown());
        });

        Disposable disposable = rabbitMQSender.send(event);
        assertTrue(latch.await(1, TimeUnit.SECONDS), "message confirm not received in time");
        disposable.dispose();

        assertEquals("order.exchange", event.getExchange());
        assertThat(capturedMessages)
                .singleElement()
                .satisfies(message -> {
                    assertEquals("order.exchange", message.getExchange());
                    assertEquals("order.created", message.getRoutingKey());
                    assertThat(new String(message.getBody())).contains("order.queue");
                });

        ArgumentCaptor<ExchangeSpecification> exchangeCaptor = ArgumentCaptor.forClass(ExchangeSpecification.class);
        verify(sender).declare(exchangeCaptor.capture());
        ExchangeSpecification exchangeSpecification = exchangeCaptor.getValue();
        assertEquals("topic", exchangeSpecification.getType());
        assertTrue(exchangeSpecification.isDurable());

        ArgumentCaptor<BindingSpecification> bindingCaptor = ArgumentCaptor.forClass(BindingSpecification.class);
        verify(sender).bind(bindingCaptor.capture());
        BindingSpecification bindingSpecification = bindingCaptor.getValue();
        assertEquals("order.exchange", bindingSpecification.getExchange());
        assertEquals("order.created", bindingSpecification.getRoutingKey());
        assertEquals("order.queue", bindingSpecification.getQueue());
    }

    @Test
    void sendShouldApplyDefaultsWhenRoutingKeyMissing() throws InterruptedException {
        TestQueueEvent event = new TestQueueEvent("invoice.queue", null, null);
        List<OutboundMessage> capturedMessages = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        when(sender.declare(any(ExchangeSpecification.class))).thenReturn(Mono.empty());
        when(sender.bind(any(BindingSpecification.class))).thenReturn(Mono.empty());
        when(sender.sendWithPublishConfirms(any())).thenAnswer(invocation -> {
            Flux<OutboundMessage> outboundFlux = invocation.getArgument(0);
            return outboundFlux
                    .doOnNext(capturedMessages::add)
                    .map(message -> new OutboundMessageResult<>(message, true))
                    .doOnNext(result -> latch.countDown());
        });

        Disposable disposable = rabbitMQSender.send(event);
        assertTrue(latch.await(1, TimeUnit.SECONDS), "message confirm not received in time");
        disposable.dispose();

        assertEquals("invoice.queue-exchange", event.getExchange());
        assertThat(capturedMessages)
                .singleElement()
                .satisfies(message -> {
                    assertEquals("invoice.queue-exchange", message.getExchange());
                    assertEquals("invoice.queue", message.getRoutingKey());
                });

        ArgumentCaptor<BindingSpecification> bindingCaptor = ArgumentCaptor.forClass(BindingSpecification.class);
        verify(sender).bind(bindingCaptor.capture());
        BindingSpecification bindingSpecification = bindingCaptor.getValue();
        assertEquals("#", bindingSpecification.getRoutingKey());
        assertEquals("invoice.queue", bindingSpecification.getQueue());
    }

    @Test
    void sendWithDelayShouldDeclareDelayedExchange() throws InterruptedException {
        TestQueueEvent event = new TestQueueEvent("delay.queue", "delay.routing", null);
        List<OutboundMessage> capturedMessages = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        when(sender.declare(any(ExchangeSpecification.class))).thenReturn(Mono.empty());
        when(sender.declare(any(QueueSpecification.class))).thenReturn(Mono.empty());
        when(sender.bind(any(BindingSpecification.class))).thenReturn(Mono.empty());
        when(sender.sendWithPublishConfirms(any())).thenAnswer(invocation -> {
            Flux<OutboundMessage> outboundFlux = invocation.getArgument(0);
            return outboundFlux
                    .doOnNext(capturedMessages::add)
                    .map(message -> new OutboundMessageResult<>(message, true))
                    .doOnNext(result -> latch.countDown());
        });

        Disposable disposable = rabbitMQSender.send(event, 5);
        assertTrue(latch.await(1, TimeUnit.SECONDS), "delayed message confirm not received in time");
        disposable.dispose();

        assertEquals("delay.queue-exchange", event.getExchange());
        assertThat(capturedMessages)
                .singleElement()
                .satisfies(message -> {
                    assertEquals("delay.queue-exchange", message.getExchange());
                    assertEquals("delay.routing", message.getRoutingKey());
                    assertThat(message.getProperties().getHeaders()).containsEntry("x-delay", 5000);
                });

        ArgumentCaptor<ExchangeSpecification> exchangeCaptor = ArgumentCaptor.forClass(ExchangeSpecification.class);
        verify(sender).declare(exchangeCaptor.capture());
        ExchangeSpecification exchangeSpecification = exchangeCaptor.getAllValues().get(0);
        assertEquals("x-delayed-message", exchangeSpecification.getType());
        assertTrue(exchangeSpecification.isDurable());
        assertThat(exchangeSpecification.getArguments()).containsEntry("x-delayed-type", "direct");
    }

    @Test
    void sendWithDelayShouldRejectNegativeSeconds() {
        TestQueueEvent event = new TestQueueEvent("delay.queue", "delay.routing", null);
        assertThat(event).isNotNull();
        assertThatThrownBy(() -> rabbitMQSender.send(event, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seconds must be greater than 0");
    }

    private static final class TestQueueEvent extends QueueEvent {

        private final String queueName;

        private TestQueueEvent(String queueName, String routingKey, String exchange) {
            this.queueName = queueName;
            setQueue(queueName);
            setRoutingKey(routingKey);
            setExchange(exchange);
        }

        @Override
        public String getQueue() {
            return queueName;
        }
    }
}

package sh.rime.reactor.rabbitmq.producer;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.AMQP;
import reactor.rabbitmq.OutboundMessageResult;
import sh.rime.reactor.rabbitmq.common.ExchangeType;
import sh.rime.reactor.rabbitmq.message.QueueEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static sh.rime.reactor.rabbitmq.common.RabbitParamConstant.*;
import static reactor.rabbitmq.ResourcesSpecification.*;

/**
 * RabbitMQ sender.
 *
 * @author youta
 **/
@Slf4j
@Component
@SuppressWarnings("unused")
public class RabbitMQSender {

    private final Sender sender;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param sender the sender
     */
    public RabbitMQSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @return Disposable
     */
    public Disposable send(QueueEvent message) {
        OutboundMessage outboundMessage;
        var exchange = getExchange(message.getExchange(), message.getQueue());
        message.setExchange(exchange);
        if (StringUtils.hasText(message.getRoutingKey())) {
            outboundMessage = new OutboundMessage(message.getExchange(), message.getRoutingKey(), message.getPayload());
        } else {
            outboundMessage = new OutboundMessage(message.getExchange(), message.getQueue(), message.getPayload());
        }
        ExchangeSpecification exchangeSpec = exchange(Objects.requireNonNullElse(message.getExchange(), ""));
        Map<String, Object> args = new HashMap<>();
        exchangeSpec.type(ExchangeType.TOPIC.getType());
        exchangeSpec.durable(true);
        exchangeSpec.arguments(args);
        String routingKey = StrUtil.emptyToDefault(message.getRoutingKey(), DEFAULT_ROUTING_KEY);
        return sender.declare(exchangeSpec)
                .then(sender.bind(binding(message.getExchange(), routingKey, message.getQueue())))
                .thenMany(sender.sendWithPublishConfirms(Flux.just(outboundMessage)))
                .doOnError(e -> log.error("RabbitMQ queue [{}] send failed", message.getQueue(), e))
                .subscribe(outboundMessageResult -> consoleLog(message, outboundMessageResult));
    }

    /**
     * 发送延迟消息
     *
     * @param message 消息
     * @param seconds 延迟时间，单位：秒
     * @return Disposable
     */
    public Disposable send(QueueEvent message, int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("seconds must be greater than 0");
        }
        Map<String, Object> map = new HashMap<>();
        map.put(DELAY_HEADER, seconds * 1000);
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)
                .contentEncoding(StandardCharsets.UTF_8.name())
                .headers(map)
                .build();
        var exchange = getExchange(message.getExchange(), message.getQueue());
        message.setExchange(exchange);
        String routingKey = StrUtil.emptyToDefault(message.getRoutingKey(), DEFAULT_ROUTING_KEY);
        OutboundMessage outboundMessage =
                new OutboundMessage(message.getExchange(), routingKey, properties, message.getPayload());
        ExchangeSpecification exchangeSpec = exchange(Objects.requireNonNullElse(message.getExchange(), ""));
        Map<String, Object> args = new HashMap<>();
        args.put(DELAYED_TYPE, ExchangeType.DIRECT.getType());
        exchangeSpec.type(ExchangeType.DELAYED.getType());
        exchangeSpec.durable(true);
        exchangeSpec.arguments(args);
        return sender.declare(exchangeSpec)
                .then(sender.declare(queue(message.getQueue()).durable(true)))
                .then(sender.bind(binding(message.getExchange(), routingKey, message.getQueue())))
                .thenMany(sender.sendWithPublishConfirms(Flux.just(outboundMessage)))
                .doOnError(e -> log.error("RabbitMQ queue [{}] send failed", message.getQueue(), e))
                .subscribe(r -> consoleLog(message, r));
    }

    /**
     * console log
     * @param message message
     * @param r result
     */
    private static void consoleLog(QueueEvent message, OutboundMessageResult<?> r) {
        if (r.isAck()) {
            log.info("RabbitMQ queue [{}] send success", message.getQueue());
        } else {
            log.error("RabbitMQ queue [{}] send failed", message.getQueue());
        }
    }

    /**
     * get exchange
     *
     * @param exchange exchange
     * @param queue    queue
     * @return exchange
     */
    private String getExchange(String exchange, String queue) {
        if (CharSequenceUtil.isEmpty(exchange)) {
            return queue + MESSAGE_DESTINATION_SUFFIX;
        }
        return exchange;
    }
}

package run.vexa.reactor.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import run.vexa.reactor.rabbitmq.message.QueueEvent;
import run.vexa.reactor.rabbitmq.properties.RabbitMQProperties;
import run.vexa.reactor.rabbitmq.exception.QueueException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;
import reactor.util.retry.Retry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;

import static reactor.rabbitmq.ResourcesSpecification.queue;

/**
 * RabbitMQ receiver.
 *
 * @param <T> the type of the queue event
 * @author youta
 **/
@Slf4j
@SuppressWarnings("unused")
public abstract class RabbitMQReceiver<T extends QueueEvent> {

    /**
     * rabbitmq receiver
     */
    @Resource
    private Receiver receiver;
    @Resource
    private Sender sender;

    /**
     * json序列化工具
     */
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RabbitMQProperties rabbitMQProperties;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    protected RabbitMQReceiver() {
    }

    /**
     * 初始化
     */
    @PostConstruct
    private void init() {
        var queue = getQueue();
        this.consume(queue).subscribe();
    }

    /**
     * 消费消息
     *
     * @param queue 队列名称
     * @return 消息流
     */
    public Flux<Void> consume(String queue) {
        return sender
                .declare(queue(queue)
                        .durable(true))
                .thenMany(receiver.consumeAutoAck(queue)
                        .flatMap(delivery ->
                                deserializeEvent(delivery.getBody())
                                        .doOnNext(event -> log.info("RabbitMQ received message {}", event))
                                        .flatMap(this::handle)
                                        .retryWhen(Retry.backoff(rabbitMQProperties.getRetry(), Duration.ofSeconds(rabbitMQProperties.getMinBackoff())))
                        ));

    }

    /**
     * 处理消息
     *
     * @param event 消息
     * @return 处理结果
     */
    public abstract Mono<Void> handle(T event);

    /**
     * 反序列化消息
     *
     * @param event 消息
     * @return 反序列化结果
     */
    private Mono<T> deserializeEvent(byte[] event) {
        return Mono.fromCallable(() -> objectMapper.readValue(event, getType()))
                .onErrorMap(JsonParseException.class, QueueException::new)
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 获取泛型类型
     *
     * @return 泛型类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getType() {
        Type type = getClass().getGenericSuperclass();
        Class<T> result = null;
        if (type instanceof ParameterizedType pType) {
            result = (Class<T>) pType.getActualTypeArguments()[0];
        }
        return result;
    }

    /**
     * 获取队列名称
     *
     * @return 队列名称
     */
    public abstract String getQueue();
}

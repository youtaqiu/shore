package io.irain.reactor.rabbitmq.autoconfigure;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.irain.reactor.rabbitmq.producer.RabbitMQSender;
import io.irain.reactor.rabbitmq.properties.RabbitMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

/**
 * @author youta
 **/
@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMqAutoConfiguration {

    /**
     * 连接配置
     *
     * @param rabbitProperties rabbitProperties
     * @return Connection
     */
    @Bean
    Mono<Connection> connectionMono(RabbitMQProperties rabbitProperties) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
        connectionFactory.useNio();
        return Mono.fromCallable(() -> connectionFactory.newConnection("shore-rabbit")).cache();
    }


    /**
     * 发送配置
     *
     * @param connectionMono   connectionMono
     * @param rabbitProperties rabbitProperties
     * @return SenderOptions
     */
    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono, RabbitMQProperties rabbitProperties) {
        if (rabbitProperties.getParsedAddresses() != null && rabbitProperties.getParsedAddresses().length > 0) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.useNio();
            connectionFactory.setUsername(rabbitProperties.getUsername());
            connectionFactory.setPassword(rabbitProperties.getPassword());
            connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
            return new SenderOptions()
                    .connectionFactory(connectionFactory)
                    .connectionSupplier(cf -> cf.newConnection(rabbitProperties.getParsedAddresses(), "shore-sender"))
                    .resourceManagementScheduler(Schedulers.boundedElastic());
        }
        return new SenderOptions()
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    /**
     * rabbitmq 发送
     *
     * @param senderOptions senderOptions
     * @return Sender
     */
    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }


    /**
     * 接收配置
     *
     * @param connectionMono   connectionMono
     * @param rabbitProperties rabbitProperties
     * @return ReceiverOptions
     */
    @Bean
    public ReceiverOptions receiverOptions(Mono<Connection> connectionMono, RabbitMQProperties rabbitProperties) {
        if (rabbitProperties.getParsedAddresses() != null && rabbitProperties.getParsedAddresses().length > 0) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.useNio();
            connectionFactory.setUsername(rabbitProperties.getUsername());
            connectionFactory.setPassword(rabbitProperties.getPassword());
            connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
            return new ReceiverOptions()
                    .connectionFactory(connectionFactory)
                    .connectionSupplier(cf -> cf.newConnection(rabbitProperties.getParsedAddresses(), "shore-receiver"))
                    .connectionSubscriptionScheduler(Schedulers.boundedElastic());
        }
        return new ReceiverOptions()
                .connectionMono(connectionMono)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());
    }


    /**
     * rabbitmq 接收
     *
     * @param receiverOptions receiverOptions
     * @return Receiver
     */
    @Bean
    public Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }

    /**
     * rabbitmq 发送
     *
     * @param sender sender
     * @return RabbitSender
     */
    @Bean
    public RabbitMQSender rabbitSender(Sender sender) {
        return new RabbitMQSender(sender);
    }

}

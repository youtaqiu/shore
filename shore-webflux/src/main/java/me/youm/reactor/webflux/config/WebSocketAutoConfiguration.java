package me.youm.reactor.webflux.config;

import me.youm.reactor.webflux.handler.WebSocketMappingHandlerMapping;
import me.youm.reactor.webflux.sender.WebSocketSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author youta
 **/
@Configuration
public class WebSocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public ConcurrentHashMap<String, WebSocketSender> senderMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public HandlerMapping webSocketMapping() {
        return new WebSocketMappingHandlerMapping();
    }

}

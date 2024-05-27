package io.irain.reactor.web.autoconfigure;

import io.irain.reactor.web.handler.WebSocketMappingHandlerMapping;
import io.irain.reactor.web.sender.WebSocketSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * webSocket自动配置.
 *
 * @author youta
 **/
@Configuration
public class WebSocketAutoConfiguration {

    /**
     * 创建WebSocketHandlerAdapter.
     * @return WebSocketHandlerAdapter
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    /**
     * 创建WebSocketSenderMap.
     * @return WebSocketSenderMap
     */
    @Bean
    public Map<String, WebSocketSender> senderMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 创建WebSocketMappingHandlerMapping.
     * @return WebSocketMappingHandlerMapping
     */
    @Bean
    public HandlerMapping webSocketMapping() {
        return new WebSocketMappingHandlerMapping();
    }

}

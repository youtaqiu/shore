package me.youm.reactor.webflux.filter;

import lombok.extern.slf4j.Slf4j;
import me.youm.reactor.common.context.ReactiveRequestContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

/**
 * @author youta
 **/
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Slf4j
public class ReactorContextWebFilter implements WebFilter {

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).contextWrite(context -> context.put(ReactiveRequestContextHolder.CONTEXT_KEY, exchange));
    }
}

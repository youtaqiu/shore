package me.youm.reactor.common.context;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author xiyu
 **/
public class ReactiveRequestContextHolder {

    public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;

    public static Mono<ServerWebExchange> getExchange() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get(CONTEXT_KEY)));
    }

}

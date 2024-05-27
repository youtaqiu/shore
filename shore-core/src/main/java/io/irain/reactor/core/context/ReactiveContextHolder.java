package io.irain.reactor.core.context;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author youta
 **/
public class ReactiveContextHolder {

    /**
     * server web exchange key
     */
    public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;

    /**
     * 获取当前线程的上下文
     *
     * @return 上下文
     */
    public static Mono<ServerWebExchange> getExchange() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get(CONTEXT_KEY)));
    }

}

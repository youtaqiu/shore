package run.vexa.reactor.core.context;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * reactive context holder
 *
 * @author youta
 **/
public final class ReactiveContextHolder {

    /**
     * Private constructor to prevent instantiation.
     */
    private ReactiveContextHolder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

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
        return Mono.deferContextual(ctx -> ctx.hasKey(CONTEXT_KEY)
                ? Mono.justOrEmpty(ctx.get(CONTEXT_KEY))
                : Mono.empty());
    }

}

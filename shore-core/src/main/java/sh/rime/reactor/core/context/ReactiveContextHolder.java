package sh.rime.reactor.core.context;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * reactive context holder
 *
 * @author youta
 **/
public class ReactiveContextHolder {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public ReactiveContextHolder() {
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

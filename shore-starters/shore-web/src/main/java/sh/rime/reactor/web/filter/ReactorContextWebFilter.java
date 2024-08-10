package sh.rime.reactor.web.filter;

import sh.rime.reactor.core.context.ReactiveContextHolder;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * ReactorContextWebFilter is a class that implements the WebFilter and Ordered interfaces.
 * It is used to put the request and response into the ContextAttributes.
 * This class has a default order of highest precedence.
 *
 * @author youta
 */
public class ReactorContextWebFilter implements WebFilter, Ordered {

    /**
     * The default order of the filter, which is the highest precedence.
     */
    public static final int DEFAULT_ORDER = HIGHEST_PRECEDENCE;

    /**
     * Constructor for the ReactorContextWebFilter class.
     */
    public ReactorContextWebFilter() {
    }

    /**
     * Get the order of the filter.
     *
     * @return the order of the filter
     */
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    /**
     * 用于将request和response放入ContextAttributes中
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @NonNull
    public final Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return chain.filter(exchange).contextWrite(context -> context.put(ReactiveContextHolder.CONTEXT_KEY, exchange));
    }
}

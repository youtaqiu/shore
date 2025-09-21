package run.vexa.reactor.security.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.vexa.reactor.commons.bean.Result;
import run.vexa.reactor.security.util.ResponseUtils;

/**
 * 权限不足处理.
 *
 * @author youta
 **/
@Component
public class AuthAccessDeniedHandler implements ServerAccessDeniedHandler {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public AuthAccessDeniedHandler() {
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        Result<Void> result = Result.failed(403, "Access denied");
        return ResponseUtils.build(exchange.getResponse(), result, 403);
    }
}

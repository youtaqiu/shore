package run.vexa.reactor.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.vexa.reactor.commons.bean.Result;
import run.vexa.reactor.security.util.ResponseUtils;

/**
 * 身份认证失败.
 *
 * @author youta
 **/
@Component
public class ReactiveAuthEntryPoint implements ServerAuthenticationEntryPoint {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public ReactiveAuthEntryPoint() {
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        Result<Void> result = Result.failed(401, "Authentication failed");
        return ResponseUtils.build(exchange.getResponse(), result, 401);
    }
}

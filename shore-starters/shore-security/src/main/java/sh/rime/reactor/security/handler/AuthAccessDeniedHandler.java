package sh.rime.reactor.security.handler;

import sh.rime.reactor.commons.exception.ServerException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 权限不足处理.
 *
 * @author youta
 **/
@Component
public class AuthAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        //抛出异常,交由全局异常处理器处理
        throw new ServerException(403,"Access denied");
    }
}

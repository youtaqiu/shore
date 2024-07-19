package sh.rime.reactor.security.handler;

import sh.rime.reactor.commons.exception.ServerException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 身份认证失败.
 *
 * @author youta
 **/
@Component
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        //抛出异常,交由全局异常处理器处理
        throw new ServerException(401,"Authentication failed");
    }
}

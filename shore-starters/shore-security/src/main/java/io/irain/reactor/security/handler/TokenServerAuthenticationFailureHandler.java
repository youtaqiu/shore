package io.irain.reactor.security.handler;

import io.irain.reactor.commons.bean.Result;
import io.irain.reactor.security.util.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author youta
 **/
@Component
public class TokenServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        Result<Void> result = Result.failed(500, exception.getMessage());
        return ResponseUtils.build(webFilterExchange.getExchange().getResponse(), result);
    }

}

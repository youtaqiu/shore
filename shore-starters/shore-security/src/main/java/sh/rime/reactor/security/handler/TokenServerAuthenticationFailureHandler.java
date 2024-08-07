package sh.rime.reactor.security.handler;

import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.security.util.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * token server authentication failure handler.
 *
 * @author youta
 **/
@Component
public class TokenServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public TokenServerAuthenticationFailureHandler() {
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        Result<Void> result = Result.failed(500, exception.getMessage());
        return ResponseUtils.build(webFilterExchange.getExchange().getResponse(), result);
    }

}

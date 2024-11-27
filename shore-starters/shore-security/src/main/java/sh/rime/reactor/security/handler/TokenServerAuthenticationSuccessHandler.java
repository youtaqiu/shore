package sh.rime.reactor.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.security.util.ResponseUtils;


/**
 * token server authentication success handler.
 *
 * @author youta
 **/
@Component
@Slf4j
public class TokenServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public TokenServerAuthenticationSuccessHandler() {
        // Do nothing
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.fromRunnable(() -> log.info("Authentication success by {}", authentication.getName()))
                .then(Mono.defer(() -> ResponseUtils.build(webFilterExchange.getExchange().getResponse(), Result.ok())));
    }

}

package sh.rime.reactor.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.security.util.ResponseUtils;

import static sh.rime.reactor.commons.enums.CommonExceptionEnum.LOGIN_TOKEN_ERROR;


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
                .onErrorResume(e -> R.error(LOGIN_TOKEN_ERROR))
                .flatMap(x -> ResponseUtils.build(webFilterExchange.getExchange().getResponse(), Result.ok()));
    }

}

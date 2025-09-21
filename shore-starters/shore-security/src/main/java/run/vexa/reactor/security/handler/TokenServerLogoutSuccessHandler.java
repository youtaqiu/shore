package run.vexa.reactor.security.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.vexa.reactor.commons.bean.R;
import run.vexa.reactor.commons.bean.Result;
import run.vexa.reactor.commons.constants.Constants;
import run.vexa.reactor.security.cache.AuthenticationCache;
import run.vexa.reactor.security.constants.TokenConstants;
import run.vexa.reactor.security.domain.CurrentUser;
import run.vexa.reactor.security.util.ResponseUtils;

import static run.vexa.reactor.commons.enums.CommonExceptionEnum.LOGOUT_TOKEN_ERROR;


/**
 * token server logout success handler.
 *
 * @author youta
 **/
@Component
public class TokenServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {

    private final AuthenticationCache<CurrentUser> authenticationCache;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param authenticationCache the authentication cache
     */
    public TokenServerLogoutSuccessHandler(AuthenticationCache<CurrentUser> authenticationCache) {
        this.authenticationCache = authenticationCache;
    }

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        return Mono.justOrEmpty(exchange.getExchange().getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(Constants.TOKEN_TYPE))
                .map(authHeader -> authHeader.substring(Constants.TOKEN_TYPE.length()))
                .flatMap(bearerToken -> authenticationCache.delete(TokenConstants.token(bearerToken))
                        .onErrorResume(ex -> R.error(LOGOUT_TOKEN_ERROR))
                        .then(authenticationCache.delete(TokenConstants.session(bearerToken)))
                        .onErrorResume(ex -> R.error(LOGOUT_TOKEN_ERROR))
                        .then(authenticationCache.delete(TokenConstants.tokenSession(bearerToken))))
                .flatMap(x -> ResponseUtils.build(exchange.getExchange().getResponse(), Result.ok()));
    }
}

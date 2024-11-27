package sh.rime.reactor.security.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.commons.constants.Constants;
import sh.rime.reactor.security.cache.AuthenticationCache;
import sh.rime.reactor.security.constants.TokenConstants;
import sh.rime.reactor.security.domain.CurrentUser;
import sh.rime.reactor.security.util.ResponseUtils;

import static sh.rime.reactor.commons.enums.CommonExceptionEnum.LOGOUT_TOKEN_ERROR;


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

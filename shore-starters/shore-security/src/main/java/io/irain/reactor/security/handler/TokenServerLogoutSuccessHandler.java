package io.irain.reactor.security.handler;

import io.irain.reactor.commons.bean.R;
import io.irain.reactor.commons.bean.Result;
import io.irain.reactor.commons.constants.Constants;
import io.irain.reactor.security.constants.TokenConstants;
import io.irain.reactor.security.repository.AuthenticationRepository;
import io.irain.reactor.security.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static io.irain.reactor.commons.enums.CommonExceptionEnum.LOGOUT_TOKEN_ERROR;


/**
 * @author youta
 **/
@Component
@RequiredArgsConstructor
public class TokenServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {

    private final AuthenticationRepository authenticationRepository;

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        return Mono.justOrEmpty(exchange.getExchange().getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(Constants.TOKEN_TYPE))
                .map(authHeader -> authHeader.substring(Constants.TOKEN_TYPE.length()))
                .flatMap(bearerToken -> authenticationRepository.delete(TokenConstants.token(bearerToken))
                        .onErrorResume(ex -> R.error(LOGOUT_TOKEN_ERROR))
                        .then(authenticationRepository.delete(TokenConstants.session(bearerToken)))
                        .onErrorResume(ex -> R.error(LOGOUT_TOKEN_ERROR))
                        .then(authenticationRepository.delete(TokenConstants.tokenSession(bearerToken))))
                .flatMap(x -> ResponseUtils.build(exchange.getExchange().getResponse(), Result.ok()));
    }
}

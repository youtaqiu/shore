package sh.rime.reactor.security.authentication;

import sh.rime.reactor.commons.constants.Constants;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.core.properties.AuthProperties;
import sh.rime.reactor.security.constants.TokenConstants;
import sh.rime.reactor.security.domain.TokenAuthentication;
import sh.rime.reactor.security.repository.AuthenticationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;


/**
 * @author youta
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class TokenServerSecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationRepository authenticationRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthProperties properties;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(Constants.TOKEN_TYPE))
                .map(authHeader -> authHeader.substring(Constants.TOKEN_TYPE.length()))
                .flatMap(token -> this.authenticationRepository.token(TokenConstants.token(token))
                        .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                        .flatMap(user -> {
                            var tokenKey = TokenConstants.token(token);
                            var sessionKey = TokenConstants.session(token);
                            return this.authenticationRepository.getExpire(sessionKey)
                                    .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                                    .map(Duration::getSeconds)
                                    .filter(expire -> expire >= 0)
                                    .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                                    .doOnNext(expire -> this.authenticationRepository.renew(expire, tokenKey, properties.getRenewTimeSeconds()))
                                    .flatMap(expire -> this.authenticationRepository.user(TokenConstants.session(token))
                                            .doOnNext(u -> log.trace("Found user: {}", user))
                                            .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                                            .map(u -> new TokenAuthentication(u, null,
                                                    u.getAuthorities()))
                                            .flatMap(authenticationManager::authenticate)
                                            .map(SecurityContextImpl::new));
                        }));
    }

}

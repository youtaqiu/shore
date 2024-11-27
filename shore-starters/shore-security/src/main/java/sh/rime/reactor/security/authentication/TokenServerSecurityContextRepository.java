package sh.rime.reactor.security.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import sh.rime.reactor.commons.constants.Constants;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.core.properties.AuthProperties;
import sh.rime.reactor.security.cache.AuthenticationCache;
import sh.rime.reactor.security.constants.TokenConstants;
import sh.rime.reactor.security.domain.CurrentUser;
import sh.rime.reactor.security.domain.TokenAuthentication;

import java.time.Duration;


/**
 * TokenServerSecurityContextRepository is a class that represents the token server security context repository.
 *
 * @author youta
 **/
@Component
@Slf4j
public class TokenServerSecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationCache<CurrentUser> authenticationCache;
    private final AuthenticationManager authenticationManager;
    private final AuthProperties properties;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param authenticationCache   the authentication cache
     * @param authenticationManager the authentication manager
     * @param properties            the properties
     */
    public TokenServerSecurityContextRepository(AuthenticationCache<CurrentUser> authenticationCache,
                                                AuthenticationManager authenticationManager,
                                                AuthProperties properties) {
        this.authenticationCache = authenticationCache;
        this.authenticationManager = authenticationManager;
        this.properties = properties;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(Constants.TOKEN_TYPE))
                .map(authHeader -> authHeader.substring(Constants.TOKEN_TYPE.length()))
                .flatMap(token -> this.authenticationCache.token(TokenConstants.token(token))
                        .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                        .flatMap(user -> {
                            var tokenKey = TokenConstants.token(token);
                            var sessionKey = TokenConstants.session(token);
                            return this.authenticationCache.getExpire(sessionKey)
                                    .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                                    .map(Duration::getSeconds)
                                    .filter(expire -> expire >= 0)
                                    .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                                    .doOnNext(expire -> this.authenticationCache.renew(tokenKey, expire, properties.getRenewTimeSeconds()))
                                    .flatMap(expire -> this.authenticationCache.user(TokenConstants.session(token))
                                            .doOnNext(u -> log.trace("Found user: {}", user))
                                            .switchIfEmpty(Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED)))
                                            .map(u -> new TokenAuthentication(u, null,
                                                    u.getAuthorities()))
                                            .flatMap(authenticationManager::authenticate)
                                            .map(SecurityContextImpl::new));
                        }));
    }

}

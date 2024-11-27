package sh.rime.reactor.security.authentication;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.commons.constants.Constants;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.core.properties.AuthProperties;
import sh.rime.reactor.core.util.BeanUtil;
import sh.rime.reactor.security.cache.AuthenticationCache;
import sh.rime.reactor.security.constants.TokenConstants;
import sh.rime.reactor.security.domain.ClientInfo;
import sh.rime.reactor.security.domain.CurrentUser;
import sh.rime.reactor.security.domain.TokenAuthentication;
import sh.rime.reactor.security.domain.TokenInfo;
import sh.rime.reactor.security.service.ClientInfoService;
import sh.rime.reactor.security.util.ResponseUtils;

import java.time.Duration;
import java.util.LinkedList;

import static sh.rime.reactor.commons.enums.CommonExceptionEnum.LOGIN_TOKEN_CACHE_ERROR;


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
    private final ObjectProvider<ClientInfoService> clientProvider;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param authenticationCache   the authentication cache
     * @param authenticationManager the authentication manager
     * @param properties            the properties
     * @param clientProvider        the client provider
     */
    public TokenServerSecurityContextRepository(AuthenticationCache<CurrentUser> authenticationCache,
                                                AuthenticationManager authenticationManager,
                                                AuthProperties properties, ObjectProvider<ClientInfoService> clientProvider) {
        this.authenticationCache = authenticationCache;
        this.authenticationManager = authenticationManager;
        this.properties = properties;
        this.clientProvider = clientProvider;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        Authentication authentication = context.getAuthentication();
        ClientInfoService clientInfoService = clientProvider.getIfAvailable(() -> new ClientInfoService() {
            @Override
            public Mono<ClientInfo> loadClientById(String clientId) {
                return Mono.just(ClientInfo.base("test", "test"));
            }
        });
        return clientInfoService.client(exchange)
                .map(clientInfo -> Tuple.of(IdUtil.fastSimpleUUID(), IdUtil.fastSimpleUUID(), clientInfo))
                .flatMap(tuple -> getToken(authentication, tuple))
                .flatMap(token -> ResponseUtils.build(exchange.getResponse(), Result.ok(token)));
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

    /**
     * Get token.
     *
     * @param authentication the authentication
     * @param tokens         the tokens
     * @return the token
     */
    private Mono<TokenInfo> getToken(Authentication authentication, Tuple3<String, String, ClientInfo> tokens) {
        CurrentUser userDetails = (CurrentUser) authentication.getPrincipal();
        if (userDetails == null) {
            return Mono.error(new ServerException(CommonExceptionEnum.UNAUTHORIZED));
        }
        if (CharSequenceUtil.isEmpty(userDetails.getUserId())) {
            userDetails.setUserId(userDetails.getId());
        }
        ClientInfo clientInfo = tokens._3;
        var tokenInfo = BeanUtil.copy(userDetails, TokenInfo.class);
        tokenInfo
                .setAccessToken(tokens._1)
                .setRefreshToken(tokens._2)
                .setExpiresIn(clientInfo.getExpire())
                .setUserId(userDetails.getId())
                .setAuthority(userDetails.getAuthorities()
                        .stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .toList());
        if (!userDetails.getRoleInfos().isEmpty()) {
            tokenInfo.setRoles(userDetails.getRoleInfos());
        }
        return this.authenticationCache.getTokenList(TokenConstants.tokenList(tokenInfo.getUsername()))
                .defaultIfEmpty(new LinkedList<>())
                .flatMap(tokensList -> {
                    if (tokensList.size() >= clientInfo.getConcurrentLoginCount()) {
                        String firstToken = tokensList.getFirst();
                        tokensList.removeFirst();
                        tokensList.add(tokens._1);
                        return this.authenticationCache.delete(TokenConstants.token(firstToken))
                                .thenReturn(tokensList);
                    }
                    tokensList.add(tokens._1);
                    return Mono.just(tokensList);
                })
                .flatMap(tokensList -> authenticationCache.tokenList(TokenConstants.tokenList(tokenInfo.getUsername()), tokensList, clientInfo.getRefreshExpire())
                        .then(
                                authenticationCache.token(TokenConstants.token(tokens._1), userDetails.getUsername(), clientInfo.getExpire())
                                        .filter(Boolean.TRUE::equals)
                                        .flatMap(x -> authenticationCache.refreshToken(TokenConstants.refresh(tokens._2), userDetails.getUsername(), clientInfo.getRefreshExpire()))
                                        .filter(Boolean.TRUE::equals)
                                        .flatMap(x -> authenticationCache.user(TokenConstants.session(tokens._1), userDetails, clientInfo.getRefreshExpire()))
                                        .filter(Boolean.TRUE::equals)
                                        .switchIfEmpty(Mono.defer(() -> R.error(LOGIN_TOKEN_CACHE_ERROR)))
                                        .map(x -> tokenInfo)
                        ));
    }

}

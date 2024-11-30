package sh.rime.reactor.security.handler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.core.util.BeanUtil;
import sh.rime.reactor.security.cache.AuthenticationCache;
import sh.rime.reactor.security.constants.TokenConstants;
import sh.rime.reactor.security.domain.ClientInfo;
import sh.rime.reactor.security.domain.CurrentUser;
import sh.rime.reactor.security.domain.TokenInfo;
import sh.rime.reactor.security.service.ClientInfoService;
import sh.rime.reactor.security.util.ResponseUtils;

import java.util.ArrayList;

import static sh.rime.reactor.commons.enums.CommonExceptionEnum.LOGIN_TOKEN_CACHE_ERROR;


/**
 * token server authentication success handler.
 *
 * @author youta
 **/
@Component
@Slf4j
public class TokenServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final AuthenticationCache<CurrentUser> authenticationCache;
    private final ObjectProvider<ClientInfoService> clientProvider;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param authenticationCache the authentication cache
     * @param clientProvider           the client provider
     */
    public TokenServerAuthenticationSuccessHandler(AuthenticationCache<CurrentUser> authenticationCache, ObjectProvider<ClientInfoService> clientProvider) {
        this.authenticationCache = authenticationCache;
        this.clientProvider = clientProvider;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {

        ClientInfoService clientInfoService = clientProvider.getIfAvailable(() -> new ClientInfoService() {
            @Override
            public Mono<ClientInfo> loadClientById(String clientId) {
                return Mono.just(ClientInfo.base("test", "test"));
            }
        });
        return clientInfoService.client(webFilterExchange.getExchange())
                .map(clientInfo -> Tuple.of(IdUtil.fastSimpleUUID(), IdUtil.fastSimpleUUID(), clientInfo))
                .flatMap(tuple -> getToken(authentication, tuple))
                .doOnNext(tokenInfo -> log.trace("Authentication success: {}", tokenInfo.getUsername()))
                .flatMap(token -> ResponseUtils.build(webFilterExchange.getExchange().getResponse(), Result.ok(token)));
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
        return this.authenticationCache.getTokenList(TokenConstants.tokenList(tokenInfo.getUsername()))
                .defaultIfEmpty(new ArrayList<>())
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

package io.irain.reactor.security.handler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import io.irain.reactor.commons.bean.R;
import io.irain.reactor.commons.bean.Result;
import io.irain.reactor.core.util.BeanUtil;
import io.irain.reactor.security.constants.TokenConstants;
import io.irain.reactor.security.domain.ClientInfo;
import io.irain.reactor.security.domain.CurrentUser;
import io.irain.reactor.security.domain.TokenInfo;
import io.irain.reactor.security.repository.AuthenticationRepository;
import io.irain.reactor.security.service.ClientInfoService;
import io.irain.reactor.security.util.ResponseUtils;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static io.irain.reactor.commons.enums.CommonExceptionEnum.LOGIN_TOKEN_CACHE_ERROR;


/**
 * @author youta
 **/
@Component
@RequiredArgsConstructor
public class TokenServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final AuthenticationRepository authenticationRepository;
    private final ObjectProvider<ClientInfoService> clientProvider;

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
                .flatMap(token -> ResponseUtils.build(webFilterExchange.getExchange().getResponse(), Result.ok(token)));
    }

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
        if (!userDetails.getRoleInfos().isEmpty()){
            tokenInfo.setRoles(userDetails.getRoleInfos());
        }
        return this.authenticationRepository.getTokenList(TokenConstants.tokenList(tokenInfo.getUsername()))
                .defaultIfEmpty(new ArrayList<>())
                .flatMap(tokensList -> {
                    if (tokensList.size() >= clientInfo.getConcurrentLoginCount()) {
                        String firstToken = tokensList.get(0);
                        tokensList.remove(0);
                        tokensList.add(tokens._1);
                        return this.authenticationRepository.delete(TokenConstants.token(firstToken))
                                .thenReturn(tokensList);
                    }
                    tokensList.add(tokens._1);
                    return Mono.just(tokensList);
                })
                .flatMap(tokensList -> authenticationRepository.tokenList(TokenConstants.tokenList(tokenInfo.getUsername()), tokensList, clientInfo.getRefreshExpire())
                        .then(
                                authenticationRepository.token(TokenConstants.token(tokens._1), userDetails.getUsername(), clientInfo.getExpire())
                                        .filter(Boolean.TRUE::equals)
                                        .flatMap(x -> authenticationRepository.refreshToken(TokenConstants.refresh(tokens._2), userDetails.getUsername(), clientInfo.getRefreshExpire()))
                                        .filter(Boolean.TRUE::equals)
                                        .flatMap(x -> authenticationRepository.user(TokenConstants.session(tokens._1), userDetails, clientInfo.getRefreshExpire()))
                                        .filter(Boolean.TRUE::equals)
                                        .switchIfEmpty(Mono.defer(() -> R.error(LOGIN_TOKEN_CACHE_ERROR)))
                                        .map(x -> tokenInfo)
                        ));
    }
}

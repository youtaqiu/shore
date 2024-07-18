package io.irain.reactor.security.authentication;

import io.irain.reactor.commons.annotation.RequestMethodEnum;
import io.irain.reactor.commons.bean.R;
import io.irain.reactor.commons.enums.CommonExceptionEnum;
import io.irain.reactor.core.properties.AuthProperties;
import io.irain.reactor.security.domain.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;


/**
 * @author youta
 **/
@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final AuthProperties properties;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        var exchange = context.getExchange();
        var requestPath = exchange.getRequest().getURI().getPath();
        var httpMethod = exchange.getRequest().getMethod();
        // 是否直接放行
        if (authenticated(httpMethod, requestPath)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        var needAuthorityList = Arrays.stream(RoleEnum.values())
                .map(RoleEnum::name)
                .toList();
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(needAuthorityList::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }


    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.defer(() -> R.error(CommonExceptionEnum.FORBIDDEN)))
                .flatMap(d -> Mono.empty());
    }


    /**
     * 是否直接放行
     *
     * @param httpMethod  http请求方法
     * @param requestPath 请求路径
     * @return 是否直接放行
     */
    private boolean authenticated(HttpMethod httpMethod, String requestPath) {
        if (requestPath != null) {
            var methodEnum = RequestMethodEnum.find(httpMethod.name());
            return properties.exclude(methodEnum, requestPath);
        }
        return false;
    }

}

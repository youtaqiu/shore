package run.vexa.reactor.security.authentication;

import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.vexa.reactor.commons.annotation.RequestMethodEnum;
import run.vexa.reactor.commons.bean.R;
import run.vexa.reactor.commons.enums.CommonExceptionEnum;
import run.vexa.reactor.core.properties.AuthProperties;
import run.vexa.reactor.security.domain.RoleEnum;

import java.util.Arrays;


/**
 * CustomAuthorizationManager is a class that represents the custom authorization manager.
 *
 * @author youta
 **/
@Component
public class CustomAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final AuthProperties properties;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param properties the properties
     */
    public CustomAuthorizationManager(AuthProperties properties) {
        this.properties = properties;
    }


    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication, AuthorizationContext context) {
        var exchange = context.getExchange();
        var requestPath = exchange.getRequest().getURI().getPath();
        var httpMethod = exchange.getRequest().getMethod();
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
                .<AuthorizationResult>map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
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

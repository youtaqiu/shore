package sh.rime.reactor.security.authentication;

import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.security.domain.SecurityExceptionEnum;
import sh.rime.reactor.security.domain.TokenAuthentication;
import sh.rime.reactor.security.grant.AuthenticationGrantManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;


/**
 * AuthenticationManager is a class that represents the authentication manager.
 *
 * @author youta
 **/
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthenticationGrantManager authenticationGrantManager;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param authenticationGrantManager the authentication grant manager
     */
    public AuthenticationManager(AuthenticationGrantManager authenticationGrantManager) {
        this.authenticationGrantManager = authenticationGrantManager;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // 已经通过验证，直接返回
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        return Mono.justOrEmpty(authentication)
                .cast(TokenAuthentication.class)
                .flatMap(this::getTokenAuthentication);

    }

    /**
     * 获取token认证
     *
     * @param tokenAuthentication {@link TokenAuthentication}
     * @return {@link TokenAuthentication}
     */
    private Mono<TokenAuthentication> getTokenAuthentication(TokenAuthentication tokenAuthentication) {
        return this.authenticationGrantManager.grant(tokenAuthentication.getLoginUser().getType(),
                        authenticationGrant -> authenticationGrant.userDetails(tokenAuthentication.getLoginUser()))
                .flatMap(userDetails -> {
                    if (!StringUtils.hasLength(userDetails.getUsername())) {
                        return R.error(SecurityExceptionEnum.USERNAME_NOT_FOUND);
                    }
                    TokenAuthentication authentications = new TokenAuthentication(userDetails, tokenAuthentication);
                    authentications.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(authentications);
                    return Mono.just(authentications);
                });
    }

}

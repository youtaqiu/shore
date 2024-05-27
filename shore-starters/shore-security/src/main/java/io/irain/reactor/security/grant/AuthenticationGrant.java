package io.irain.reactor.security.grant;

import io.irain.reactor.security.domain.CurrentUser;
import io.irain.reactor.commons.enums.GrantType;
import io.irain.reactor.security.domain.LoginRequest;
import io.irain.reactor.security.domain.TokenAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

/**
 * 认证授权
 *
 * @author youta
 **/
public interface AuthenticationGrant {

    /**
     * grant
     *
     * @return grant
     */
    default String grant() {
        return GrantType.PASSWORD.getValue();
    }

    /**
     * authentication token
     *
     * @param request {@link LoginRequest}
     * @return {@link TokenAuthentication}
     */
    Mono<Authentication> authentication(LoginRequest request);

    /**
     * 获取用户详情
     * @param request {@link LoginRequest}
     * @return {@link UserDetails}
     */
    Mono<CurrentUser> userDetails(LoginRequest request);


}

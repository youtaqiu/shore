package sh.rime.reactor.security.grant;

import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.core.util.OptionalBean;
import sh.rime.reactor.security.domain.CurrentUser;
import sh.rime.reactor.security.domain.LoginRequest;
import sh.rime.reactor.security.domain.SecurityExceptionEnum;
import sh.rime.reactor.security.domain.TokenAuthentication;
import sh.rime.reactor.security.service.UserDetailService;
import sh.rime.reactor.security.service.SimpleUserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * @author youta
 **/
@Service
@RequiredArgsConstructor
public class PasswordAuthenticationGrant implements AuthenticationGrant {

    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserDetailService> userDetailServices;

    @Override
    public Mono<Authentication> authentication(LoginRequest request) {
        return Mono.just(new TokenAuthentication(
                OptionalBean.ofNullable(request)
                        .getBean(LoginRequest::getUsername),
                OptionalBean.ofNullable(request)
                        .getBean(LoginRequest::getPassword))
                .setLoginUser(request));
    }

    @Override
    public Mono<CurrentUser> userDetails(LoginRequest request) {
        UserDetailService userDetailService = this.userDetailServices.getIfAvailable(SimpleUserDetailServiceImpl::new);
        return userDetailService
                .loadByUsername(request.getUsername())
                .switchIfEmpty(Mono.defer(() -> R.error(SecurityExceptionEnum.USERNAME_NOT_FOUND)))
                .filter(user -> userDetailService.customMatcher() ?
                        userDetailService.matches(user, request.getPassword(), request.getType()) :
                        passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.defer(() -> R.error(SecurityExceptionEnum.PASSWORD_NOT_MATCH)));
    }

}

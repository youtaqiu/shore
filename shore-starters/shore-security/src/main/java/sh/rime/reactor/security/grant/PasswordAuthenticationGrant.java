package sh.rime.reactor.security.grant;

import sh.rime.reactor.commons.bean.R;
import sh.rime.reactor.core.util.OptionalBean;
import sh.rime.reactor.security.domain.CurrentUser;
import sh.rime.reactor.security.domain.LoginRequest;
import sh.rime.reactor.security.domain.SecurityExceptionEnum;
import sh.rime.reactor.security.domain.TokenAuthentication;
import sh.rime.reactor.security.service.UserDetailService;
import sh.rime.reactor.security.service.SimpleUserDetailServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * PasswordAuthenticationGrant is a class that represents the password authentication grant.
 *
 * @author youta
 **/
@Service
public class PasswordAuthenticationGrant implements AuthenticationGrant {

    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserDetailService> userDetailServices;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param passwordEncoder    the password encoder
     * @param userDetailServices the user detail services
     */
    public PasswordAuthenticationGrant(PasswordEncoder passwordEncoder, ObjectProvider<UserDetailService> userDetailServices) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailServices = userDetailServices;
    }

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

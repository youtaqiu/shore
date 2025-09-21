package run.vexa.reactor.security.grant;

import run.vexa.reactor.commons.bean.R;
import run.vexa.reactor.core.util.OptionalBean;
import run.vexa.reactor.security.domain.CurrentUser;
import run.vexa.reactor.security.domain.LoginRequest;
import run.vexa.reactor.security.domain.SecurityExceptionEnum;
import run.vexa.reactor.security.domain.TokenAuthentication;
import run.vexa.reactor.security.service.UserDetailService;
import run.vexa.reactor.security.service.SimpleUserDetailServiceImpl;
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
                .filter(user -> userDetailService.customMatcher()
                        ? userDetailService.matches(user, request.getPassword(), request.getType())
                        : passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.defer(() -> R.error(SecurityExceptionEnum.PASSWORD_NOT_MATCH)));
    }

}

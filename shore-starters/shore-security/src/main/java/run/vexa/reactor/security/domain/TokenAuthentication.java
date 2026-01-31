package run.vexa.reactor.security.domain;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * TokenAuthentication is a class that represents token authentication information.
 *
 * @author youta
 **/
@Getter
@Accessors(chain = true)
public class TokenAuthentication extends AbstractAuthenticationToken {

    /**
     * principal
     */
    private final Object principal;

    /**
     * credentials
     */
    private final Object credentials;

    /**
     * 登录用户
     */
    private LoginRequest loginRequest;

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 获取登录用户
     *
     * @return loginUser
     */
    public LoginRequest getLoginUser() {
        return this.loginRequest;
    }

    /**
     * 设置登录用户
     *
     * @param loginRequest loginUser
     * @return TokenAuthentication
     */
    public TokenAuthentication setLoginUser(LoginRequest loginRequest) {
        this.loginRequest = loginRequest;
        return this;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    /**
     * 构造器
     *
     * @param principal   principal
     * @param credentials credentials
     */
    public TokenAuthentication(Object principal, Object credentials) {
        super(Collections.emptyList());
        this.principal = principal;
        this.credentials = credentials;
    }

    /**
     * 构造器
     *
     * @param principal   principal
     * @param credentials credentials
     * @param authorities authorities
     */
    public TokenAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    /**
     * 构造器
     *
     * @param principal    principal
     * @param credentials  credentials
     * @param authorities  authorities
     * @param loginRequest loginUser
     */
    public TokenAuthentication(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials, LoginRequest loginRequest) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.loginRequest = loginRequest;
    }

}

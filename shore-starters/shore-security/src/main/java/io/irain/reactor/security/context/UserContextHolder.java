package io.irain.reactor.security.context;

import io.irain.reactor.commons.exception.ServerException;
import io.irain.reactor.commons.exception.TokenException;
import io.irain.reactor.core.util.OptionalBean;
import io.irain.reactor.security.domain.CurrentUser;
import io.irain.reactor.security.domain.TokenAuthentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

/**
 * @author youta
 **/
public class UserContextHolder {

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public static Mono<String> userId() {
        return user()
                .map(user -> OptionalBean.ofNullable(user).getBean(CurrentUser::getUserId).orElseGet(() -> user.getId()))
                .switchIfEmpty(Mono.error(new ServerException("userid must not empty!")));
    }

    /**
     * 获取用户id，如果不存在则返回默认值
     *
     * @return 用户id
     */
    public static Mono<String> userIdDefault() {
        return userDefault()
                .defaultIfEmpty(CurrentUser.builder()
                        .userId("")
                        .build())
                .map(CurrentUser::getUserId);
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    public static Mono<CurrentUser> user() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .cast(TokenAuthentication.class)
                .map(TokenAuthentication::getPrincipal)
                .cast(CurrentUser.class)
                .switchIfEmpty(Mono.error(new TokenException("Invalid token")));
    }

    /**
     * 获取token
     *
     * @return token
     */
    public static Mono<String> token() {
        return userDefault()
                .defaultIfEmpty(CurrentUser.builder()
                        .accessToken("")
                        .build())
                .map(CurrentUser::getAccessToken);
    }


    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    private static Mono<CurrentUser> userDefault() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .cast(TokenAuthentication.class)
                .map(TokenAuthentication::getPrincipal)
                .cast(CurrentUser.class)
                .switchIfEmpty(Mono.empty());
    }

}

package io.irain.reactor.security.service;

import io.irain.reactor.security.domain.CurrentUser;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;


/**
 * 用户详情服务实现类
 *
 * @author youta
 **/
@SuppressWarnings("unused")
public interface UserDetailService extends ReactiveUserDetailsService {

    /**
     * 根据用户名查找用户
     *
     * @param username the username
     * @return UserDetails 用户详情
     */
    default Mono<UserDetails> findByUsername(String username) {
        return null;
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return CurrentUser 用户详情
     */
    Mono<CurrentUser> loadByUsername(String username);

    /**
     * 根据手机号登录
     *
     * @param phone 手机号
     * @return UserDetails 用户详情
     */
    default Mono<CurrentUser> loadUserByPhone(String phone) {
        return Mono.empty();
    }

    /**
     * 根据社交账号登录
     *
     * @param openId 第三方的绑定的openId
     * @return UserDetails 用户详情
     */
    default Mono<CurrentUser> loadUserByOpenId(String openId) {
        return Mono.empty();
    }

    /**
     * check identity
     *
     * @param currentUser current user
     * @param code        code or password (when password mode is password, when other mode is verification code))
     * @param grantType   grant type
     * @return boolean
     */
    default boolean matches(CurrentUser currentUser, String code, String grantType) {
        return false;
    }

    /**
     * 自定义匹配器
     *
     * @return Boolean 是否自定义匹配器
     */
    default Boolean customMatcher() {
        return false;
    }
}

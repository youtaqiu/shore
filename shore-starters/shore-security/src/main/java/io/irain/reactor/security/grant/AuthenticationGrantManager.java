package io.irain.reactor.security.grant;

import io.irain.reactor.commons.exception.ServerException;
import io.irain.reactor.core.util.OptionalBean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static io.irain.reactor.commons.enums.GrantType.PASSWORD;


/**
 * @author youta
 **/
@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationGrantManager {

    /**
     * 授权策略
     */
    private final List<AuthenticationGrant> strategies;

    /**
     * 初始化授权
     * @param grantType 授权类型
     * @return 授权策略
     */
    public Optional<AuthenticationGrant> initialize(String grantType) {
        return this.strategies.stream()
                .filter(handler -> Objects.equals(handler.grant(), grantType))
                .findAny();
    }

    /**
     * 授权
     *
     * @param grantType 授权类型
     * @param function  授权函数
     * @param <T>       授权结果类型
     * @return 授权结果
     */
    public <T> T grant(String grantType, Function<AuthenticationGrant, T> function) {
        return this.initialize(OptionalBean.ofNullable(grantType).orElseGet(PASSWORD::getValue))
                .map(function)
                .orElseThrow(() -> new ServerException("Invalid grant type"));
    }

}

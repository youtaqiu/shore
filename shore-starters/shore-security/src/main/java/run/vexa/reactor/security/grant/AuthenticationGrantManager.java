package run.vexa.reactor.security.grant;

import jakarta.annotation.Resource;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.core.util.OptionalBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

import static run.vexa.reactor.commons.enums.GrantType.PASSWORD;


/**
 * AuthenticationGrantManager is a class that represents authentication grant manager.
 *
 * @author youta
 **/
@Service
public class AuthenticationGrantManager {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param strategies the strategies
     */
    public AuthenticationGrantManager(List<AuthenticationGrant> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    /**
     * 授权策略
     */
    @Resource
    private List<AuthenticationGrant> strategies;

    /**
     * 初始化授权
     *
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

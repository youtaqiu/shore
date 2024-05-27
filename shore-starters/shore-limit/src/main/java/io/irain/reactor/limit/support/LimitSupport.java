package io.irain.reactor.limit.support;

import io.irain.reactor.core.context.ReactiveContextHolder;
import io.irain.reactor.core.spel.SpringExpressionResolver;
import io.irain.reactor.core.util.ReactiveAddrUtil;
import io.irain.reactor.limit.annotation.Limit;
import io.irain.reactor.limit.provider.LimitProvider;
import io.irain.reactor.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author youta
 **/
@RequiredArgsConstructor(staticName = "of")
public class LimitSupport {

    /**
     * SpEL表达式解析器
     */
    private final SpringExpressionResolver resolver = new SpringExpressionResolver();


    private final LimitProvider limitProvider;

    /**
     * Exec boolean.
     *
     * @param limit  the limit
     * @param method the method
     * @param args   the args
     * @return the boolean
     */
    public Mono<Boolean> exec(Limit limit, Method method, Object[] args) {
        return ReactiveContextHolder.getExchange()
                .map(ServerWebExchange::getRequest)
                .flatMap(request -> {
                    String key = limit.key();
                    int rate = limit.rate();
                    long expire = limit.expire();
                    TimeUnit rateIntervalUnit = limit.unit();
                    String spELKey = resolver.evaluate(key, method, args);
                    String realIP = null;
                    if (limit.restrictIp()) {
                        realIP = ReactiveAddrUtil.getRemoteAddr(request);
                    }
                    if (realIP != null) {
                        spELKey = spELKey + "#" + realIP;
                    }
                    if (limit.restrictUser()) {
                        String finalSpELKey = spELKey;
                        return UserContextHolder.userIdDefault()
                                .defaultIfEmpty("0")
                                .map(userId -> finalSpELKey + "#" + userId)
                                .flatMap(userIdKey -> limitProvider.tryAcquire(userIdKey, rate, expire, rateIntervalUnit));
                    }
                    return limitProvider.tryAcquire(spELKey, rate, expire, rateIntervalUnit);
                });
    }

}

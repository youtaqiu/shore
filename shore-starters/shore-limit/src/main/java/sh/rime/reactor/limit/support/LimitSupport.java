package sh.rime.reactor.limit.support;

import sh.rime.reactor.core.context.ReactiveContextHolder;
import sh.rime.reactor.core.spel.SpringExpressionResolver;
import sh.rime.reactor.core.util.ReactiveAddrUtil;
import sh.rime.reactor.limit.annotation.Limit;
import sh.rime.reactor.limit.provider.LimitProvider;
import sh.rime.reactor.security.context.UserContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;

/**
 * 限流支持
 *
 * @author youta
 **/
public class LimitSupport {

    /**
     * SpEL表达式解析器
     */
    private final SpringExpressionResolver resolver = new SpringExpressionResolver();


    private final LimitProvider limitProvider;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param limitProvider the limit provider
     */
    public LimitSupport(LimitProvider limitProvider) {
        this.limitProvider = limitProvider;
    }

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
                    ChronoUnit rateIntervalUnit = limit.unit();
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

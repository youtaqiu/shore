package sh.rime.reactor.limit.aspect;

import sh.rime.reactor.commons.enums.TimeUnitMessageKey;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.limit.annotation.Limit;
import sh.rime.reactor.limit.provider.LimitProvider;
import sh.rime.reactor.limit.support.LimitSupport;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author youta
 **/
@Aspect
@RequiredArgsConstructor
@Order(2)
public class LimitAspect {

    private final ObjectProvider<LimitProvider> provider;

    /**
     * Handler object.
     *
     * @param point the point
     * @param limit the limit
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("@annotation(limit)")
    @SuppressWarnings("all")
    public Object handler(ProceedingJoinPoint point, Limit limit) throws Throwable {
        Signature signature = point.getSignature();
        if (!(signature instanceof MethodSignature methodSignature)) {
            return point.proceed();
        }
        Method method = methodSignature.getMethod();
        Object[] args = point.getArgs();
        LimitSupport limitSupport = LimitSupport.of(provider.getIfAvailable());
        var msg = "Do not repeat the request, please wait for {0} {1} and try again";
        msg = MessageFormat.format(msg, limit.expire(), getTimeUnitName(limit.unit()));
        return limitSupport.exec(limit, method, args)
                .filter(allowed -> allowed)
                .switchIfEmpty(Mono.error(new ServerException(429, msg)))
                .flatMap(allowed -> result(point));

    }

    private static Mono<?> result(ProceedingJoinPoint point) {
        try {
            Object result = point.proceed();
            if (result instanceof Mono<?> mono) {
                return mono;
            } else if (result instanceof Flux<?> flux) {
                return flux.collectList();
            } else {
                return Mono.just(result);
            }
        } catch (Throwable throwable) {
            return Mono.error(throwable);
        }
    }


    /**
     * The Message source.
     */
    protected final MessageSource messageSource;

    /**
     * 获取加锁时间单位显示名称.
     *
     * @param timeUnit 分布式锁注解
     * @return 加锁时间单位显示名称
     */
    protected String getTimeUnitName(TimeUnit timeUnit) {
        try {
            return TimeUnitMessageKey.getKey(timeUnit);
        } catch (NoSuchMessageException ignore) {
            return "";
        }
    }

}

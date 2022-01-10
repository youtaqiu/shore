package me.youm.reactor.auth.aspect;

import lombok.AllArgsConstructor;
import me.youm.reactor.auth.context.CurrentUserContext;
import me.youm.reactor.common.exception.AuthorityException;
import me.youm.reactor.common.utils.StringPool;
import me.youm.reactor.security.annotation.PreAuth;
import me.youm.reactor.security.enums.SuperAdmin;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author youta
 **/
@Aspect
@Component
@AllArgsConstructor
public class PreAuthAspect {

    private static final String ALL_PERMISSION = "*:*:*";

    @Around("@annotation(me.youm.reactor.security.annotation.PreAuth)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        PreAuth preAuth = method.getAnnotation(PreAuth.class);
        if (ObjectUtils.isEmpty(preAuth)) {
            return point.proceed();
        }
        String perm = preAuth.value();
        if (!StringUtils.hasText(perm)) {
            return point.proceed();
        }
        try {
            Mono<?> mono = (Mono<?>) point.proceed();
            return CurrentUserContext.reactiveUser().flatMap(tokenInfo -> {
                if (!SuperAdmin.ADMIN.getValue().equalsIgnoreCase(tokenInfo.getUsername()) && StringUtils.hasText(perm)) {
                    if (!hasPermissions(Arrays.stream(String.valueOf(tokenInfo.getAuthorities()).split(StringPool.COMMA)).collect(Collectors.toList()), perm)) {
                        return Mono.error(new AuthorityException("无权访问"));
                    }
                }
                return mono;
            });
        } catch (Throwable e) {
            return CurrentUserContext.reactiveUser().flatMap(tokenInfo -> {
                if (!SuperAdmin.ADMIN.getValue().equalsIgnoreCase(tokenInfo.getUsername()) && StringUtils.hasText(perm)) {
                    if (!hasPermissions(Arrays.stream(String.valueOf(tokenInfo.getAuthorities()).split(StringPool.COMMA)).collect(Collectors.toList()), perm)) {
                        return Mono.error(new AuthorityException("无权访问"));
                    }
                }
                return Mono.error(e);
            });
        }
    }

    /**
     * 判断是否包含权限
     *
     * @param authorities 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Collection<String> authorities, String permission) {
        return authorities.stream().filter(StringUtils::hasText)
                .anyMatch(x -> ALL_PERMISSION.contains(x) || PatternMatchUtils.simpleMatch(permission, x));
    }

}

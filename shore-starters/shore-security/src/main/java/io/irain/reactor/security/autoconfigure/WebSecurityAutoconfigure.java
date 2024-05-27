package io.irain.reactor.security.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import io.irain.reactor.commons.annotation.Anonymous;
import io.irain.reactor.commons.annotation.RequestMethodEnum;
import io.irain.reactor.core.properties.AuthProperties;
import io.irain.reactor.security.authentication.AuthenticationManager;
import io.irain.reactor.security.authentication.CustomAuthorizationManager;
import io.irain.reactor.security.authentication.PostLoginAuthConverter;
import io.irain.reactor.security.authentication.TokenServerSecurityContextRepository;
import io.irain.reactor.security.handler.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * WebSecurityConfig
 *
 * @author youta
 */
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@ComponentScan(basePackages = {"io.irain.reactor.security.authentication",
        "io.irain.reactor.security.grant",
        "io.irain.reactor.security.handler",
        "io.irain.reactor.security.repository"
})
public class WebSecurityAutoconfigure {

    private final AuthenticationManager authenticationManager;
    private final TokenServerSecurityContextRepository tokenServerSecurityContextRepository;
    private final TokenServerAuthenticationSuccessHandler tokenServerAuthenticationSuccessHandler;
    private final TokenServerAuthenticationFailureHandler tokenServerAuthenticationFailureHandler;
    private final TokenServerLogoutSuccessHandler tokenServerLogoutSuccessHandler;
    private final AuthEntryPoint authEntryPoint;
    private final AuthAccessDeniedHandler authAccessDeniedHandler;
    private final CustomAuthorizationManager customAuthorizationManager;
    private final PostLoginAuthConverter postLoginAuthConverter;
    private final AuthProperties authProperties;

    /**
     * requestMappingHandlerMapping
     */
    protected final RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * 配置认证管理器
     *
     * @param httpSecurity http
     * @return 认证管理器
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        loadAnonymousUrls();
        httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(tokenServerSecurityContextRepository)
                .authorizeExchange(exchange -> {
                    if (!authProperties.getEnable()) {
                        exchange.anyExchange().permitAll();
                        return;
                    }
                    exchange
                            .pathMatchers(HttpMethod.OPTIONS).permitAll()
                            .anyExchange().access(customAuthorizationManager);
                })
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .accessDeniedHandler(authAccessDeniedHandler)
                        .authenticationEntryPoint(authEntryPoint)
                )
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .logout(logoutSpec -> logoutSpec.logoutUrl(authProperties.getLogoutPattern())
                        .logoutSuccessHandler(tokenServerLogoutSuccessHandler));
        return httpSecurity.build();
    }

    private void loadAnonymousUrls() {
        RequestMappingHandlerMapping handlerMapping = SpringUtil.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            // 处理注解信息
            boolean needLogin = getAnnotation(handlerMethod, Anonymous.class) == null;
            if (!needLogin) {
                Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
                RequestMethodEnum request = RequestMethodEnum.find(methods.isEmpty()
                        ? RequestMethodEnum.ALL.getType()
                        : methods.stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Method not found"))
                        .name());
                setPath(request, requestMappingInfo);
            }
        }
    }

    private void setPath(RequestMethodEnum method, RequestMappingInfo requestMappingInfo) {
        Set<PathPattern> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        if (patterns.isEmpty()) {
            return;
        }
        switch (Objects.requireNonNull(method)) {
            case GET -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getGetExcludePatterns().addAll(collect);
            }
            case POST -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getPostExcludePatterns().addAll(collect);
            }
            case PUT -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getPutExcludePatterns().addAll(collect);
            }
            case PATCH -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getPatchExcludePatterns().addAll(collect);
            }
            case DELETE -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getDeleteExcludePatterns().addAll(collect);
            }
            default -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getExcludePatterns().addAll(collect);
            }
        }

    }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        filter.setSecurityContextRepository(tokenServerSecurityContextRepository);
        filter.setServerAuthenticationConverter(postLoginAuthConverter);
        filter.setAuthenticationSuccessHandler(tokenServerAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(tokenServerAuthenticationFailureHandler);
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, authProperties.getLoginPattern()));

        return filter;
    }

    /**
     * 用户信息验证管理器，可按需求添加多个按顺序执行
     */
    @Bean("customReactiveAuthenticationManager")
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }


    /**
     * 获取注解.
     *
     * @param handlerMethod   处理方法
     * @param annotationClass 注解类
     * @param <A>             注解类
     * @return 注解对象
     */
    @SuppressWarnings("SameParameterValue")
    protected static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationClass) {
        A annotation = handlerMethod.getMethodAnnotation(annotationClass);
        if (annotation == null) {
            Class<?> clazz = handlerMethod.getBeanType();
            annotation = clazz.getAnnotation(annotationClass);

            if (annotation == null) {
                annotation = clazz.getPackage().getAnnotation(annotationClass);
            }
        }
        return annotation;
    }

}

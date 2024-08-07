package sh.rime.reactor.http.aot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import sh.rime.reactor.http.core.HttpExchangeClientFactoryBean;

import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Http exchange client bean factory initialization aot processor.
 *
 * @author rained
 **/
@Slf4j
public class HttpExchangeClientBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

    private final GenericApplicationContext context;

    private final Map<String, BeanDefinition> httpExchangeClientBeanDefinitions;

    private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

    /**
     * constructor
     *
     * @param context {@link GenericApplicationContext}
     */
    public HttpExchangeClientBeanFactoryInitializationAotProcessor(GenericApplicationContext context) {
        this.context = context;
        this.httpExchangeClientBeanDefinitions = getHttpExchangeClientBeanDefinitions();
    }

    private Map<String, BeanDefinition> getHttpExchangeClientBeanDefinitions() {
        return context.getBeansOfType(HttpExchangeClientFactoryBean.class)
                .keySet()
                .stream()
                .map(beanName -> {
                    String originalBeanName = BeanFactoryUtils.transformedBeanName(beanName);
                    return Map.entry(originalBeanName, context.getBeanDefinition(originalBeanName));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * "org.springframework.web.service.annotation.HttpExchangeBeanRegistrationAotProcessor"
     * "org.springframework.web.service.annotation.HttpExchangeReflectiveProcessor"
     *
     * @param beanFactory the bean factory to process
     * @return {@link BeanFactoryInitializationAotContribution}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        ConfigurableListableBeanFactory contextBeanFactory = context.getBeanFactory();
        if (httpExchangeClientBeanDefinitions.isEmpty() || !beanFactory.equals(contextBeanFactory)) {
            return null;
        }
        return (generationContext, beanFactoryInitializationCode) -> {
            RuntimeHints hints = generationContext.getRuntimeHints();
            Collection<BeanDefinition> beanDefinitions = httpExchangeClientBeanDefinitions.values();
            for (BeanDefinition beanDefinition : beanDefinitions) {
                PropertyValue httpExchangeClientInterface = beanDefinition.getPropertyValues().getPropertyValue("httpExchangeClientInterface");
                if (Objects.nonNull(httpExchangeClientInterface)) {
                    Class<?> clazz = (Class<?>) httpExchangeClientInterface.getValue();
                    if (Objects.nonNull(clazz)) {
                        hints.proxies().registerJdkProxy(AopProxyUtils.completeJdkProxyInterfaces(clazz));
                        registerMethodHints(hints.reflection(), clazz);
                    }
                }
            }
        };
    }

    private void registerMethodHints(ReflectionHints hints, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            registerMethodHints(hints, method);
        }
    }

    private void registerMethodHints(ReflectionHints hints, Method method) {
        for (Parameter parameter : method.getParameters()) {
            registerParameterTypeHints(hints, MethodParameter.forParameter(parameter));
        }
        registerReturnTypeHints(hints, MethodParameter.forExecutable(method, -1));
    }

    /**
     * register type hints for return type
     *
     * @param hints               {@link ReflectionHints}
     * @param returnTypeParameter {@link MethodParameter}
     */
    protected void registerReturnTypeHints(ReflectionHints hints, MethodParameter returnTypeParameter) {
        if (!void.class.equals(returnTypeParameter.getParameterType())) {
            this.bindingRegistrar.registerReflectionHints(hints, returnTypeParameter.getGenericParameterType());
        }
    }

    /**
     * register type hints for parameters annotated with @RequestBody
     *
     * @param hints           {@link ReflectionHints}
     * @param methodParameter {@link MethodParameter}
     */
    protected void registerParameterTypeHints(ReflectionHints hints, MethodParameter methodParameter) {
        if (methodParameter.hasParameterAnnotation(RequestBody.class)) {
            this.bindingRegistrar.registerReflectionHints(hints, methodParameter.getGenericParameterType());
        }
    }
}

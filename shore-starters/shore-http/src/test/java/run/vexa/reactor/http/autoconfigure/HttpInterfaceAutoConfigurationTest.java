package run.vexa.reactor.http.autoconfigure;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import run.vexa.reactor.http.aot.HttpExchangeClientBeanFactoryInitializationAotProcessor;
import run.vexa.reactor.http.core.HttpExchangeClientFactoryBean;
import run.vexa.reactor.http.core.LoadBalancerExchangeFilterFunctionsConsumer;
import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HttpInterfaceAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(HttpInterfaceAutoConfiguration.class)
            .withBean(CustomLoadBalancerExchangeFilterFunction.class, () -> mock(CustomLoadBalancerExchangeFilterFunction.class));

    @Test
    void beansAreRegistered() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(LoadBalancerExchangeFilterFunctionsConsumer.class);
            assertThat(context).hasSingleBean(HttpExchangeClientBeanFactoryInitializationAotProcessor.class);
            assertThat(context).hasSingleBean(HttpInterfaceAutoConfiguration.HttpExchangeClientFactoryBeanPostProcessor.class);
        });
    }

    @Test
    void postProcessorResolvesGenericType() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        HttpInterfaceAutoConfiguration.HttpExchangeClientFactoryBeanPostProcessor postProcessor =
                new HttpInterfaceAutoConfiguration.HttpExchangeClientFactoryBeanPostProcessor();
        postProcessor.setBeanFactory(beanFactory);

        CapturingRootBeanDefinition beanDefinition = new CapturingRootBeanDefinition(GenericHttpExchangeClientFactoryBean.class);
        beanDefinition.getPropertyValues().add("httpExchangeClientInterface", SampleHttpClient.class);
        ResolvableType initialType = beanDefinition.getResolvableType();
        assertThat(initialType.hasUnresolvableGenerics()).isTrue();

        try (MockedStatic<ClassUtils> mocked = Mockito.mockStatic(ClassUtils.class)) {
            mocked.when(() -> ClassUtils.isPresent(Mockito.anyString(), Mockito.any())).thenReturn(true);
            postProcessor.postProcessMergedBeanDefinition(beanDefinition, HttpExchangeClientFactoryBean.class, "httpExchangeClientFactoryBean");
        }

        ResolvableType updatedType = beanDefinition.getResolvableType();
        assertThat(updatedType.hasUnresolvableGenerics()).isFalse();
        ResolvableType expectedType = ResolvableType.forClassWithGenerics(GenericHttpExchangeClientFactoryBean.class, SampleHttpClient.class);
        assertThat(beanDefinition.capturedTargetType()).isEqualTo(expectedType);
        assertThat(beanDefinition.getResolvableType()).isEqualTo(expectedType);
    }

    private interface SampleHttpClient {
    }

    private static final class GenericHttpExchangeClientFactoryBean<T> extends HttpExchangeClientFactoryBean {
    }

    private static final class CapturingRootBeanDefinition extends RootBeanDefinition {

        private ResolvableType currentType;
        private ResolvableType capturedTargetType;
        private final Class<?> capturedBeanClass;

        private CapturingRootBeanDefinition(Class<?> beanClass) {
            super(beanClass);
            this.capturedBeanClass = beanClass;
            this.currentType = ResolvableType.forClass(beanClass);
        }

        @Override
        public ResolvableType getResolvableType() {
            return currentType;
        }

        @Override
        public Class<?> getBeanClass() {
            return this.capturedBeanClass;
        }

        @Override
        public void setTargetType(ResolvableType targetType) {
            this.capturedTargetType = targetType;
            this.currentType = targetType;
        }

        private ResolvableType capturedTargetType() {
            return capturedTargetType;
        }
    }
}

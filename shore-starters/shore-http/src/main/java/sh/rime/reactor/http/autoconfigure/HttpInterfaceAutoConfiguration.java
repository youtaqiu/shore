package sh.rime.reactor.http.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import sh.rime.reactor.http.aot.HttpExchangeClientBeanFactoryInitializationAotProcessor;
import sh.rime.reactor.http.core.HttpExchangeClientFactoryBean;
import org.springframework.context.annotation.Bean;
import sh.rime.reactor.http.core.LoadBalancerExchangeFilterFunctionsConsumer;

/**
 * Http interface auto configuration.
 *
 * @author youta
 **/
@AutoConfiguration
@Slf4j
public class HttpInterfaceAutoConfiguration {


    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public HttpInterfaceAutoConfiguration() {
    }

    /**
     * Load balancer exchange filter functions consumer load balancer exchange filter functions consumer.
     *
     * @return the load balancer exchange filter functions consumer
     */
    @Bean
    public LoadBalancerExchangeFilterFunctionsConsumer loadBalancerExchangeFilterFunctionsConsumer() {
        return new LoadBalancerExchangeFilterFunctionsConsumer();
    }

    @Bean
    static HttpExchangeClientBeanFactoryInitializationAotProcessor httpExchangeClientBeanFactoryInitializationAotProcessor(GenericApplicationContext applicationContext) {
        return new HttpExchangeClientBeanFactoryInitializationAotProcessor(applicationContext);
    }

    @Bean
    static HttpExchangeClientFactoryBeanPostProcessor httpExchangeClientFactoryBeanPostProcessor() {
        return new HttpExchangeClientFactoryBeanPostProcessor();
    }

    /**
     * borrowed from "org.mybatis.spring.nativex.MyBatisMapperFactoryBeanPostProcessor"
     */
    @SuppressWarnings("NullableProblems")
    static class HttpExchangeClientFactoryBeanPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {

        private static final String HTTP_EXCHANGE_CLIENT_FACTORY_BEAN = "io.github.llnancy.httpexchange.core.HttpExchangeClientFactoryBean";

        private ConfigurableBeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }

        @Override
        public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
            if (ClassUtils.isPresent(HTTP_EXCHANGE_CLIENT_FACTORY_BEAN, this.beanFactory.getBeanClassLoader())) {
                resolveHttpExchangeClientFactoryBeanTypeIfNecessary(beanDefinition);
            }
        }

        private void resolveHttpExchangeClientFactoryBeanTypeIfNecessary(RootBeanDefinition beanDefinition) {
            if (!beanDefinition.hasBeanClass() || !HttpExchangeClientFactoryBean.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                return;
            }
            if (beanDefinition.getResolvableType().hasUnresolvableGenerics()) {
                Class<?> httpExchangeClientInterface = getHttpExchangeClientInterface(beanDefinition);
                if (httpExchangeClientInterface != null) {
                    // Exposes a generic type information to context for prevent early initializing
                    beanDefinition
                            .setTargetType(ResolvableType.forClassWithGenerics(beanDefinition.getBeanClass(), httpExchangeClientInterface));
                }
            }
        }

        private Class<?> getHttpExchangeClientInterface(RootBeanDefinition beanDefinition) {
            try {
                return (Class<?>) beanDefinition.getPropertyValues().get("httpExchangeClientInterface");
            } catch (Exception e) {
                log.debug("Fail getting httpExchangeClient interface type.", e);
                return null;
            }
        }
    }

}

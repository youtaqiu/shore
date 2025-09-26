package run.vexa.reactor.http.core;

import org.junit.jupiter.api.Test;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import run.vexa.reactor.http.testclients.scanner.BasicScanHttpClient;
import run.vexa.reactor.http.testclients.scanner.PrototypeScanHttpClient;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathHttpExchangeClientScannerTest {

    private static ClassPathHttpExchangeClientScanner newScanner(DefaultListableBeanFactory beanFactory) {
        ClassPathHttpExchangeClientScanner scanner = new ClassPathHttpExchangeClientScanner(beanFactory, ClassPathHttpExchangeClientScannerTest.class.getClassLoader());
        scanner.registerFilters();
        return scanner;
    }

    @Test
    void scanRegistersHttpExchangeClientFactoryBean() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ClassPathHttpExchangeClientScanner scanner = newScanner(beanFactory);

        scanner.scan("run.vexa.reactor.http.testclients.scanner");

        assertTrue(beanFactory.containsBeanDefinition("basicScanHttpClient"));
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("basicScanHttpClient");
        assertEquals(HttpExchangeClientFactoryBean.class.getName(), beanDefinition.getBeanClassName());
        assertEquals(BasicScanHttpClient.class, beanDefinition.getPropertyValues().get("httpExchangeClientInterface"));
    }

    @Test
    void scanAppliesDefaultScopeWhenProvided() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ClassPathHttpExchangeClientScanner scanner = newScanner(beanFactory);
        scanner.setDefaultScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE);

        scanner.scan("run.vexa.reactor.http.testclients.scanner");

        assertTrue(beanFactory.containsBeanDefinition("singletonScanHttpClient"));
        BeanDefinition proxyDefinition = beanFactory.getBeanDefinition("singletonScanHttpClient");
        assertEquals(ScopedProxyFactoryBean.class.getName(), proxyDefinition.getBeanClassName());

        BeanDefinition targetDefinition = beanFactory.getBeanDefinition("scopedTarget.singletonScanHttpClient");
        assertEquals(HttpExchangeClientFactoryBean.class.getName(), targetDefinition.getBeanClassName());
        assertEquals(ConfigurableBeanFactory.SCOPE_PROTOTYPE, targetDefinition.getScope());
    }

    @Test
    void scanCreatesScopedProxyForNonSingletonDefinitions() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ClassPathHttpExchangeClientScanner scanner = newScanner(beanFactory);

        scanner.scan("run.vexa.reactor.http.testclients.scanner");

        assertTrue(beanFactory.containsBeanDefinition("prototypeScanHttpClient"));
        assertTrue(beanFactory.containsBeanDefinition("scopedTarget.prototypeScanHttpClient"));

        BeanDefinition proxyDefinition = beanFactory.getBeanDefinition("prototypeScanHttpClient");
        assertEquals(ScopedProxyFactoryBean.class.getName(), proxyDefinition.getBeanClassName());

        BeanDefinition targetDefinition = beanFactory.getBeanDefinition("scopedTarget.prototypeScanHttpClient");
        assertEquals(HttpExchangeClientFactoryBean.class.getName(), targetDefinition.getBeanClassName());
        assertEquals(PrototypeScanHttpClient.class, targetDefinition.getPropertyValues().get("httpExchangeClientInterface"));
    }
}

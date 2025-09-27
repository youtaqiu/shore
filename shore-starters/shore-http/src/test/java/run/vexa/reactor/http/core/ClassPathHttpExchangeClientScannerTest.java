package run.vexa.reactor.http.core;

import org.junit.jupiter.api.Test;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import run.vexa.reactor.http.testclients.scanner.BasicScanHttpClient;
import run.vexa.reactor.http.testclients.scanner.PrototypeScanHttpClient;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

class ClassPathHttpExchangeClientScannerTest {

    private static ClassPathHttpExchangeClientScanner newScanner(DefaultListableBeanFactory beanFactory) {
        ClassPathHttpExchangeClientScanner scanner = new ClassPathHttpExchangeClientScanner(beanFactory, ClassPathHttpExchangeClientScannerTest.class.getClassLoader());
        scanner.registerFilters();
        return scanner;
    }

    @Test
    void allowsCustomFactoryBeanClassAndResetsToDefaultWhenNull() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ClassPathHttpExchangeClientScanner scanner = newScanner(beanFactory);
        scanner.setHttpExchangeClientFactoryBeanClass(CustomHttpExchangeClientFactoryBean.class);
        scanner.scan("run.vexa.reactor.http.testclients.scanner");

        AbstractBeanDefinition customDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition("basicScanHttpClient");
        assertEquals(CustomHttpExchangeClientFactoryBean.class.getName(), customDefinition.getBeanClassName());

        DefaultListableBeanFactory beanFactoryWithDefault = new DefaultListableBeanFactory();
        ClassPathHttpExchangeClientScanner defaultScanner = newScanner(beanFactoryWithDefault);
        defaultScanner.setHttpExchangeClientFactoryBeanClass(null);
        defaultScanner.scan("run.vexa.reactor.http.testclients.scanner");

        AbstractBeanDefinition defaultDefinition = (AbstractBeanDefinition) beanFactoryWithDefault.getBeanDefinition("basicScanHttpClient");
        assertEquals(HttpExchangeClientFactoryBean.class.getName(), defaultDefinition.getBeanClassName());
    }

    @Test
    void isCandidateComponentReturnsTrueOnlyForAnnotatedInterfaces() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        TestableScanner scanner = new TestableScanner(beanFactory);

        AnnotatedGenericBeanDefinition annotatedInterface =
                new AnnotatedGenericBeanDefinition(BasicScanHttpClient.class);
        assertTrue(scanner.applyIsCandidateComponent(annotatedInterface));

        AnnotatedGenericBeanDefinition regularClass =
                new AnnotatedGenericBeanDefinition(String.class);
        assertFalse(scanner.applyIsCandidateComponent(regularClass));
    }

    @Test
    void processBeanDefinitionsResolvesScopedProxyDecoratedDefinition() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ClassPathHttpExchangeClientScanner scanner = newScanner(beanFactory);

        RootBeanDefinition targetDefinition = new RootBeanDefinition();
        targetDefinition.setBeanClassName(BasicScanHttpClient.class.getName());

        RootBeanDefinition proxyDefinition = new RootBeanDefinition();
        proxyDefinition.setBeanClassName(ScopedProxyFactoryBean.class.getName());
        proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, "basicScanHttpClient"));

        BeanDefinitionHolder holder = new BeanDefinitionHolder(proxyDefinition, "proxyBasicScanHttpClient");
        Set<BeanDefinitionHolder> holders = new HashSet<>();
        holders.add(holder);

        Method process = ClassPathHttpExchangeClientScanner.class.getDeclaredMethod("processBeanDefinitions", Set.class);
        process.setAccessible(true);
        process.invoke(scanner, holders);

        assertEquals(HttpExchangeClientFactoryBean.class, targetDefinition.getBeanClass());
        assertEquals(BasicScanHttpClient.class,
                targetDefinition.getPropertyValues().get("httpExchangeClientInterface"));
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

    private static final class CustomHttpExchangeClientFactoryBean extends HttpExchangeClientFactoryBean {
    }

    private static final class TestableScanner extends ClassPathHttpExchangeClientScanner {
        TestableScanner(DefaultListableBeanFactory registry) {
            super(registry, ClassPathHttpExchangeClientScannerTest.class.getClassLoader());
            registerFilters();
        }

        boolean applyIsCandidateComponent(AnnotatedGenericBeanDefinition definition) {
            return super.isCandidateComponent(definition);
        }
    }
}

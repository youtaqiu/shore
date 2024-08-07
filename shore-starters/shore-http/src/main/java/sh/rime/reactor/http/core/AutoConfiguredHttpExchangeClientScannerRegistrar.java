package sh.rime.reactor.http.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import sh.rime.reactor.http.annotation.HttpClient;

import java.util.List;

/**
 * Register the HttpExchangeClient interface to the BeanDefinitionRegistry through the ClassPathHttpExchangeClientScanner.
 *
 * @author rained
 **/
@Slf4j
public class AutoConfiguredHttpExchangeClientScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware {

    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;
    private BeanFactory beanFactory;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public AutoConfiguredHttpExchangeClientScannerRegistrar() {
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        if (!AutoConfigurationPackages.has(this.beanFactory)) {
            log.warn("Could not determine auto-configuration package, automatic http exchange client scanning disabled.");
            return;
        }
        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        // Scan the @HttpExchangeClient annotated interface under the specified path and register it to the BeanDefinitionRegistry
        ClassPathHttpExchangeClientScanner scanner = new ClassPathHttpExchangeClientScanner(registry, classLoader);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        scanner.registerFilters();
        // Scan and register to BeanDefinition
        scanner.scan(StringUtils.toStringArray(packages));
    }

}

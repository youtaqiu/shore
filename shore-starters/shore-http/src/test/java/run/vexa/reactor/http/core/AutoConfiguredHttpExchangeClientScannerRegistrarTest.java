package run.vexa.reactor.http.core;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutoConfiguredHttpExchangeClientScannerRegistrarTest {

    @Test
    void doesNothingWhenAutoConfigurationPackagesMissing() {
        AutoConfiguredHttpExchangeClientScannerRegistrar registrar = new AutoConfiguredHttpExchangeClientScannerRegistrar();
        BeanFactory beanFactory = mock(BeanFactory.class);
        registrar.setBeanFactory(beanFactory);
        registrar.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
        registrar.setResourceLoader(mock(ResourceLoader.class));

        AnnotationMetadata metadata = mock(AnnotationMetadata.class);
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);

        try (MockedStatic<AutoConfigurationPackages> autoPackages = org.mockito.Mockito.mockStatic(AutoConfigurationPackages.class);
             MockedConstruction<ClassPathHttpExchangeClientScanner> construction = org.mockito.Mockito.mockConstruction(ClassPathHttpExchangeClientScanner.class)) {

            autoPackages.when(() -> AutoConfigurationPackages.has(beanFactory)).thenReturn(false);

            registrar.registerBeanDefinitions(metadata, registry);

            assertThat(construction.constructed()).isEmpty();
        }
    }

    @Test
    void scansDiscoveredPackages() {
        AutoConfiguredHttpExchangeClientScannerRegistrar registrar = new AutoConfiguredHttpExchangeClientScannerRegistrar();
        BeanFactory beanFactory = mock(BeanFactory.class);
        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        registrar.setBeanFactory(beanFactory);
        registrar.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
        registrar.setResourceLoader(resourceLoader);

        AnnotationMetadata metadata = mock(AnnotationMetadata.class);
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        List<String> packages = List.of("run.vexa.reactor.http.testclients", "run.vexa.reactor.http.core");

        try (MockedStatic<AutoConfigurationPackages> autoPackages = org.mockito.Mockito.mockStatic(AutoConfigurationPackages.class);
             MockedConstruction<ClassPathHttpExchangeClientScanner> construction = org.mockito.Mockito.mockConstruction(
                     ClassPathHttpExchangeClientScanner.class,
                     (mockScanner, context) -> when(mockScanner.scan(any(String[].class))).thenReturn(0))) {

            autoPackages.when(() -> AutoConfigurationPackages.has(beanFactory)).thenReturn(true);
            autoPackages.when(() -> AutoConfigurationPackages.get(beanFactory)).thenReturn(packages);

            registrar.registerBeanDefinitions(metadata, registry);

            assertThat(construction.constructed()).hasSize(1);
            ClassPathHttpExchangeClientScanner scanner = construction.constructed().getFirst();
            verify(scanner).setResourceLoader(resourceLoader);
            verify(scanner).registerFilters();
            ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);
            verify(scanner).scan(captor.capture());
            List<String> actualPackages = Arrays.asList(captor.getValue());
            assertThat(actualPackages).containsAll(packages);
        }
    }
}

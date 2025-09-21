package run.vexa.reactor.core.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Application context utils unit test.
 *
 * @author rained
 **/
class ApplicationContextUtilsTest {

    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        context = mock(ApplicationContext.class);
    }

    @Test
    void testGetBeanFromContext() {
        MyService service = new MyService();
        when(context.getBean(MyService.class)).thenReturn(service);

        MyService result = ApplicationContextUtils.getBeanOrReflect(context, MyService.class);
        assertEquals(service, result);
    }

    @Test
    void testCreateInstanceByReflection() {
        when(context.getBean(MyService.class)).thenThrow(new RuntimeException("Bean not found"));

        MyService result = ApplicationContextUtils.getBeanOrReflect(context, MyService.class);
        assertNotNull(result);
    }

    @Test
    void testCreateInstanceByStaticMethod() {
        when(context.getBean(MyService.class)).thenThrow(new RuntimeException("Bean not found"));
        MyService result = ApplicationContextUtils.getBeanOrReflect(context, MyService.class);
        assertNotNull(result);
    }

    @Test
    void testFailureInAllMethods() {
        when(context.getBean(MyServiceWithoutConstructorOrFactory.class))
                .thenThrow(new RuntimeException("Bean not found"));

        assertThrows(RuntimeException.class, () ->
                ApplicationContextUtils.getBeanOrReflect(context, MyServiceWithoutConstructorOrFactory.class));
    }

    /**
     * sample service class
     */
    static class MyService {
        MyService() {
        }

    }

    /**
     * sample service class without constructor or factory method
     */
    static final class MyServiceWithoutConstructorOrFactory {
        private MyServiceWithoutConstructorOrFactory() {
            throw new UnsupportedOperationException("Cannot instantiate this class");
        }
    }
}


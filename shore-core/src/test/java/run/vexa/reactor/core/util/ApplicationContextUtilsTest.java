package run.vexa.reactor.core.util;


import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void testPrivateConstructor() {
        // Verify the constructor is private
        var constructor = assertDoesNotThrow(() -> ApplicationContextUtils.class.getDeclaredConstructor((Class<?>[]) null));
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
            "Constructor should be private");

        // Verify instantiation throws UnsupportedOperationException
        var exception = assertThrows(InvocationTargetException.class, () -> {
            // Try to create instance through reflection
            try {
                constructor.setAccessible(true);
                constructor.newInstance();
            } finally {
                // Reset accessibility
                constructor.setAccessible(false);
            }
        });

        // Verify the exception type and message
        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        assertEquals("This is a utility class and cannot be instantiated", 
            exception.getCause().getMessage());
    }

    @Test
    void testCreateInstanceByStaticMethod() {
        // When context.getBean fails and constructor fails, it should try the create method
        when(context.getBean(ServiceWithCreateMethod.class)).thenThrow(new RuntimeException("Bean not found"));

        ServiceWithCreateMethod result = ApplicationContextUtils.getBeanOrReflect(context, ServiceWithCreateMethod.class);
        assertNotNull(result);
        assertTrue(result.isCreatedByStaticMethod());
    }

    @Test
    void testFailureInAllMethods() {
        when(context.getBean(ServiceWithoutConstructorOrFactory.class))
                .thenThrow(new RuntimeException("Bean not found"));

        assertThrows(RuntimeException.class, () -> ApplicationContextUtils.getBeanOrReflect(context, ServiceWithoutConstructorOrFactory.class));
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
    static final class ServiceWithoutConstructorOrFactory {
        private ServiceWithoutConstructorOrFactory() {
            throw new UnsupportedOperationException("Cannot instantiate this class");
        }
    }

    /**
     * Sample service class with static create method
     */
    @Getter
    @SuppressWarnings("unused")
    static final class ServiceWithCreateMethod {
        private final boolean createdByStaticMethod;

        private ServiceWithCreateMethod() {
            // Make constructor fail
            throw new UnsupportedOperationException("Use create() method");
        }

        private ServiceWithCreateMethod(boolean createdByStaticMethod) {
            this.createdByStaticMethod = createdByStaticMethod;
        }

        public static ServiceWithCreateMethod create() {
            return new ServiceWithCreateMethod(true);
        }

    }
}


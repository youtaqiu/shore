package sh.rime.reactor.core.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * SpringUtils unit test.
 *
 * @author rained
 **/
class SpringUtilsTest {

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(SpringUtils.class, "context", applicationContext);
        when(applicationContext.getEnvironment()).thenReturn(environment);
    }


    @Test
    void testGetBean() {
        MyBean myBean = new MyBean();
        when(applicationContext.getBean(MyBean.class)).thenReturn(myBean);

        MyBean result = SpringUtils.getBean(MyBean.class);
        assertEquals(myBean, result);
    }

    @Test
    void testGetProperty() {
        when(environment.getProperty("my.property")).thenReturn("myValue");

        String result = SpringUtils.getProperty("my.property");
        assertEquals("myValue", result);
    }

    /**
     * 测试用的 Bean
     */
    static class MyBean {
    }
}



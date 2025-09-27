package run.vexa.reactor.core.spel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEvaluationContextFactoryTest {

    private DefaultEvaluationContextFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultEvaluationContextFactory();
    }

    @Test
    void evalRegistersMethodParameters() throws Exception {
        Method method = SampleTarget.class.getDeclaredMethod("sampleMethod", String.class, int.class);
        EvaluationContext ctx = factory.eval(method, new Object[]{"Alice", 28}, Map.of());

        StandardEvaluationContext standardContext = (StandardEvaluationContext) ctx;
        String[] parameterNames = getCachedParameterNames(method);
        assertNotNull(parameterNames);
        assertEquals(2, parameterNames.length);
        assertEquals("Alice", standardContext.lookupVariable(parameterNames[0]));
        assertEquals(28, standardContext.lookupVariable(parameterNames[1]));
    }

    @Test
    void evalSkipsWhenArgsLengthMismatch() throws Exception {
        Method method = SampleTarget.class.getDeclaredMethod("sampleMethod", String.class, int.class);
        EvaluationContext ctx = factory.eval(method, new Object[]{"only-one"}, Map.of());

        StandardEvaluationContext standardContext = (StandardEvaluationContext) ctx;
        String[] parameterNames = getCachedParameterNames(method);
        assertNotNull(parameterNames);
        for (String name : parameterNames) {
            assertNull(standardContext.lookupVariable(name));
        }
    }

    @Test
    void evalMergesExpandMap() throws Exception {
        Method method = SampleTarget.class.getDeclaredMethod("sampleMethod", String.class, int.class);
        EvaluationContext ctx = factory.eval(method, new Object[]{"Alice", 28}, Map.of("extra", "value"));

        StandardEvaluationContext standardContext = (StandardEvaluationContext) ctx;
        assertEquals("value", standardContext.lookupVariable("extra"));
    }

    @Test
    void evalWithNullMethodUsesExpandMapOnly() {
        EvaluationContext ctx = factory.eval(null, null, Map.of("foo", "bar"));
        StandardEvaluationContext standardContext = (StandardEvaluationContext) ctx;
        assertEquals("bar", standardContext.lookupVariable("foo"));
    }

    @Test
    void parameterNamesAreCached() throws Exception {
        Method method = SampleTarget.class.getDeclaredMethod("sampleMethod", String.class, int.class);
        factory.eval(method, new Object[]{"Alice", 28}, Map.of());
        factory.eval(method, new Object[]{"Bob", 30}, Map.of());

        String[] parameterNames = getCachedParameterNames(method);
        assertNotNull(parameterNames);
        assertEquals(2, parameterNames.length);
        assertSame(parameterNames, getCachedParameterNames(method));
        assertEquals(1, getParameterNameCacheSize());
    }

    private String[] getCachedParameterNames(Method method) {
        @SuppressWarnings("unchecked")
        Map<Method, String[]> cache = (Map<Method, String[]>) ReflectionTestUtils.getField(factory, "parameterNamesCache");
        return cache != null ? cache.get(method) : null;
    }

    private int getParameterNameCacheSize() {
        @SuppressWarnings("unchecked")
        Map<Method, String[]> cache = (Map<Method, String[]>) ReflectionTestUtils.getField(factory, "parameterNamesCache");
        return cache != null ? cache.size() : 0;
    }

    private static final class SampleTarget {
        @SuppressWarnings("unused")
        void sampleMethod(String name, int age) {
            // no-op
        }
    }
}

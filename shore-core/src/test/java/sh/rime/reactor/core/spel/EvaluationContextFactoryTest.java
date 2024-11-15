package sh.rime.reactor.core.spel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Evaluation context factory unit test.
 *
 * @author rained
 **/
class EvaluationContextFactoryTest {

    private EvaluationContextFactory contextFactory;

    @BeforeEach
    void setUp() {
        contextFactory = EvaluationContextFactory.INSTANCE;
    }

    @Test
    void testEvalWithMethodAndArgs() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("dummyMethod", String.class, int.class);
        Object[] args = {"test", 42};

        EvaluationContext context = contextFactory.eval(method, args);

        assertNotNull(context);
        assertEquals("test", context.lookupVariable("arg0"));
        assertEquals(42, context.lookupVariable("arg1"));
    }

    @Test
    void testEvalWithExpandMap() {
        Map<String, Object> expandMap = Map.of("key", "value");

        EvaluationContext context = contextFactory.eval(expandMap);

        assertNotNull(context);
        assertEquals("value", context.lookupVariable("key"));
    }

    // Dummy method for reflection
    @SuppressWarnings("unused")
    public void dummyMethod(String arg0, int arg1) {
        // do nothing
    }
}

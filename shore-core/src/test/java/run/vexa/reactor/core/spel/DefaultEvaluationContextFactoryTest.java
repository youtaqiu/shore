package run.vexa.reactor.core.spel;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Default evaluation context factory unit test.
 * @author rained
 **/
class DefaultEvaluationContextFactoryTest {

    private DefaultEvaluationContextFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultEvaluationContextFactory();
    }


    @Test
    void testExpandMapInjection() throws NoSuchMethodException {
        Method method = SampleClass.class.getMethod("sampleMethod", String.class, int.class);
        Map<String, Object> expandMap = Map.of("extraVar", "extraValue");
        EvaluationContext context = factory.eval(method, new Object[]{"test", 123}, expandMap);
        assertEquals("extraValue", context.lookupVariable("extraVar"));
    }


    @Test
    void testEvaluationContextProperties() {
        StandardEvaluationContext context = (StandardEvaluationContext) factory.eval(null, null, null);
        assertNotNull(context.getPropertyAccessors());
        assertFalse(context.getPropertyAccessors().isEmpty());
        assertNotNull(context.getTypeLocator());
        assertNotNull(context.getTypeConverter());
    }

    @SuppressWarnings("unused")
    static class SampleClass {
        public void sampleMethod(String param1, int param2) {
            // do nothing
        }
    }
}


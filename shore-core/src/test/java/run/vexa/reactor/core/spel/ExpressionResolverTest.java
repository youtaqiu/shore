package run.vexa.reactor.core.spel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The type Expression resolver test.
 *
 * @author youta
 */
class ExpressionResolverTest {
    private SpringExpressionResolver expressionResolver;
    private Environment environmentMock;

    @BeforeEach
    void setUp() {
        environmentMock = mock(Environment.class);
        expressionResolver = new SpringExpressionResolver(environmentMock);
    }

    @Test
    void testEvaluateWithMethodAndArgs() throws NoSuchMethodException {
        String expression = "Hello #{#name}";
        Method method = this.getClass().getMethod("dummyMethod", String.class);
        Object[] args = {"World"};

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("name", "World");

        when(environmentMock.resolvePlaceholders(expression))
                .thenReturn(expression);

        String result = expressionResolver.evaluate(expression, method, args, String.class);

        assertEquals("Hello World", result);
    }

    @Test
    void testEvaluateWithExpandMap() {
        String expression = "Hello #{#name}";
        Map<String, Object> expandMap = new HashMap<>();
        expandMap.put("name", "World");

        EvaluationContext context = new StandardEvaluationContext();
        expandMap.forEach(context::setVariable);

        when(environmentMock.resolvePlaceholders(expression))
                .thenReturn(expression);

        String result = expressionResolver.evaluate(expression, expandMap, String.class);

        assertEquals("Hello World", result);
    }

    @Test
    void testEvaluateWithMethodArgsAndExpandMap() throws NoSuchMethodException {
        String expression = "Hello #{#name}";
        Method method = this.getClass().getMethod("dummyMethod", String.class);
        Object[] args = {"World"};
        Map<String, Object> expandMap = new HashMap<>();
        expandMap.put("name", "World");

        EvaluationContext context = new StandardEvaluationContext();
        expandMap.forEach(context::setVariable);

        when(environmentMock.resolvePlaceholders(expression))
                .thenReturn(expression);

        String result = expressionResolver.evaluate(expression, method, args, expandMap, String.class);

        assertEquals("Hello World", result);
    }

    // Dummy method for reflection
    @SuppressWarnings("unused")
    public void dummyMethod(String name) {
        // do nothing
    }
}

package sh.rime.reactor.core.spel;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Spring expression resolver unit test.
 *
 * @author rained
 **/
class SpringExpressionResolverTest {

    private SpringExpressionResolver resolver;
    private EvaluationContext context;
    private Environment environment;

    @BeforeEach
    void setUp() {
        resolver = new SpringExpressionResolver();
        context = new StandardEvaluationContext();
        environment = mock(Environment.class);
    }


    @Test
    void testExpressionWithVariables() {
        context.setVariable("value", 3);
        String expression = "#value * 2";
        Integer result = resolver.evaluate(expression, context, Integer.class);
        assertEquals(6, result);
    }

    @Test
    void testPlaceholderResolution() {
        resolver.setEnvironment(environment);
        when(environment.resolvePlaceholders("${my.property}")).thenReturn("resolvedValue");

        String expression = "${my.property}";
        String result = resolver.evaluate(expression, context, String.class);

        assertEquals("resolvedValue", result);
        verify(environment, times(1)).resolvePlaceholders("${my.property}");
    }


    @Test
    void testInvalidExpression() {
        String expression = "invalid + 1";

        assertThrows(ExpressionException.class, () -> resolver.evaluate(expression, context, Integer.class));
    }

    @Test
    void testEvaluateWithValidExpression() {
        String expression = "Hello #{#name}";
        EvaluationContext evalContext = new StandardEvaluationContext();
        evalContext.setVariable("name", "World");

        when(environment.resolvePlaceholders(expression))
                .thenReturn("Hello #{#name}");

        String result = resolver.evaluate(expression, evalContext, String.class);

        assertEquals("Hello World", result);
    }

    @Test
    void testEvaluateWithInvalidExpression() {
        String expression = "Hello #{#unknown}";

        EvaluationContext evalContext = new StandardEvaluationContext();

        when(environment.resolvePlaceholders(expression))
                .thenReturn("Hello #{#unknown}");

        assertThrows(ExpressionException.class, () -> resolver.evaluate(expression, evalContext, Integer.class));
    }

    @Test
    void testEvaluateWithNoVariables() {
        String expression = "Hello World";
        EvaluationContext evalContext = new StandardEvaluationContext();

        when(environment.resolvePlaceholders(expression))
                .thenReturn("Hello World");

        String result = resolver.evaluate(expression, evalContext, String.class);

        assertEquals("Hello World", result);
    }

}


package run.vexa.reactor.http.aot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import run.vexa.reactor.http.core.HttpExchangeClientFactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HttpExchangeClientBeanFactoryInitializationAotProcessorTest {

    private GenericApplicationContext context;

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void returnsNullWhenNoHttpExchangeClients() {
        context = new GenericApplicationContext();
        context.refresh();

        HttpExchangeClientBeanFactoryInitializationAotProcessor processor =
                new HttpExchangeClientBeanFactoryInitializationAotProcessor(context);

        BeanFactoryInitializationAotContribution contribution = processor.processAheadOfTime(context.getBeanFactory());
        assertThat(contribution).isNull();
    }

    @Test
    void returnsNullWhenBeanFactoryDoesNotMatch() {
        registerHttpExchangeClientFactory();

        HttpExchangeClientBeanFactoryInitializationAotProcessor processor =
                new HttpExchangeClientBeanFactoryInitializationAotProcessor(context);

        BeanFactoryInitializationAotContribution contribution =
                processor.processAheadOfTime(new DefaultListableBeanFactory());

        assertThat(contribution).isNull();
    }

    @Test
    void registersHintsForReturnTypesAndRequestBodies() throws Exception {
        registerHttpExchangeClientFactory();

        RecordingProcessor processor = new RecordingProcessor(context);

        BeanFactoryInitializationAotContribution contribution =
                processor.processAheadOfTime(context.getBeanFactory());
        assertThat(contribution).isNotNull();

        RuntimeHints runtimeHints = new RuntimeHints();
        GenerationContext generationContext = createGenerationContext(runtimeHints);
        BeanFactoryInitializationCode initializationCode = createBeanFactoryInitializationCode();
        contribution.applyTo(generationContext, initializationCode);

        assertThat(processor.returnTypeRegistrations).contains(SampleResponse.class);
        assertThat(processor.parameterTypeRegistrations).contains(SampleRequest.class);
    }

    private void registerHttpExchangeClientFactory() {
        context = new GenericApplicationContext();
        RootBeanDefinition beanDefinition = new RootBeanDefinition(HttpExchangeClientFactoryBean.class);
        beanDefinition.getPropertyValues().add("httpExchangeClientInterface", SampleHttpClient.class);
        context.registerBeanDefinition("sampleHttpClientFactory", beanDefinition);
        context.refresh();
    }

    interface SampleHttpClient {

        SampleResponse submit(@org.springframework.web.bind.annotation.RequestBody SampleRequest request);
    }

    static class SampleRequest {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class SampleResponse {
        private String result;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    private static final class RecordingProcessor extends HttpExchangeClientBeanFactoryInitializationAotProcessor {

        private final List<Class<?>> parameterTypeRegistrations = new ArrayList<>();
        private final List<Class<?>> returnTypeRegistrations = new ArrayList<>();

        private RecordingProcessor(GenericApplicationContext context) {
            super(context);
        }

        @Override
        protected void registerParameterTypeHints(ReflectionHints hints, org.springframework.core.MethodParameter methodParameter) {
            parameterTypeRegistrations.add(methodParameter.getParameterType());
            super.registerParameterTypeHints(hints, methodParameter);
        }

        @Override
        protected void registerReturnTypeHints(ReflectionHints hints, org.springframework.core.MethodParameter returnTypeParameter) {
            returnTypeRegistrations.add(returnTypeParameter.getParameterType());
            super.registerReturnTypeHints(hints, returnTypeParameter);
        }
    }

    private static GenerationContext createGenerationContext(RuntimeHints hints) {
        return (GenerationContext) Proxy.newProxyInstance(
                GenerationContext.class.getClassLoader(),
                new Class<?>[]{GenerationContext.class},
                new GenerationContextInvocationHandler(hints));
    }

    private static BeanFactoryInitializationCode createBeanFactoryInitializationCode() {
        return mock(BeanFactoryInitializationCode.class);
    }

    private static final class GenerationContextInvocationHandler implements InvocationHandler {

        private final RuntimeHints runtimeHints;
        private final Map<Class<?>, Object> interfaceCache = new ConcurrentHashMap<>();

        private GenerationContextInvocationHandler(RuntimeHints runtimeHints) {
            this.runtimeHints = runtimeHints;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Object result = null;
            String methodName = method.getName();
            if ("getRuntimeHints".equals(methodName)) {
                result = runtimeHints;
            } else {
                Class<?> returnType = method.getReturnType();
                if (returnType.isPrimitive()) {
                    result = primitiveDefault(returnType);
                } else if (returnType.isInterface()) {
                    result = interfaceCache.computeIfAbsent(returnType, GenerationContextInvocationHandler::createInterfaceProxy);
                }
            }
            return result;
        }

        private static Object createInterfaceProxy(Class<?> iface) {
            InvocationHandler handler = (p, m, a) -> {
                Class<?> returnType = m.getReturnType();
                return returnType.isPrimitive() ? primitiveDefault(returnType) : null;
            };
            return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, handler);
        }

        private static Object primitiveDefault(Class<?> primitiveType) {
            Object value;
            switch (primitiveType.getName()) {
                case "boolean" -> value = Boolean.FALSE;
                case "byte" -> value = Byte.valueOf((byte) 0);
                case "short" -> value = Short.valueOf((short) 0);
                case "char" -> value = Character.valueOf('\0');
                case "int" -> value = Integer.valueOf(0);
                case "long" -> value = Long.valueOf(0L);
                case "float" -> value = Float.valueOf(0F);
                case "double" -> value = Double.valueOf(0D);
                default -> value = null;
            }
            return value;
        }
    }
}

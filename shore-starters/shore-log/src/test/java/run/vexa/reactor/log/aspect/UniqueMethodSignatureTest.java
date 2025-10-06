package run.vexa.reactor.log.aspect;

import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniqueMethodSignatureTest {

    @Test
    void shouldUseUnderlyingMethodForEquality() throws Exception {
        Method method = SampleClass.class.getDeclaredMethod("sample");
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);

        UniqueMethodSignature signature1 = new UniqueMethodSignature(signature);
        UniqueMethodSignature signature2 = new UniqueMethodSignature(signature);

        assertThat(signature1).isEqualTo(signature2);
        assertThat(signature1.hashCode()).isEqualTo(signature2.hashCode());
        assertThat(signature1.getMethodSignature()).isSameAs(signature);
    }

    private static final class SampleClass {
        private void sample() {
            // no-op
        }
    }
}

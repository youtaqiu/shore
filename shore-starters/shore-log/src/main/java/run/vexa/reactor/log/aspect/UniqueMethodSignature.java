package run.vexa.reactor.log.aspect;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import static lombok.AccessLevel.NONE;

/**
 * A unique method signature.
 * This class is used to uniquely identify a method signature.
 * It is used to cache the method signature and the method being logged.
 *
 * @author rained
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class UniqueMethodSignature {

    /**
     * The method signature.
     */
    private final MethodSignature methodSignature;

    /**
     * The method being logged.
     */
    @Getter(NONE)
    @EqualsAndHashCode.Include
    private final Method method;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param methodSignature the method signature
     */
    public UniqueMethodSignature(MethodSignature methodSignature) {
        this.methodSignature = methodSignature;
        this.method = methodSignature.getMethod();
    }
}

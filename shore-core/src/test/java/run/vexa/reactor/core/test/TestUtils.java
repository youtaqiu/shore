package run.vexa.reactor.core.test;

import org.junit.jupiter.api.Assertions;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;

/**
 * Test utility class providing helper methods for testing.
 * 
 * @author ve-xa
 */
@SuppressWarnings("unused")
public final class TestUtils {

    private TestUtils() {
        // Prevent instantiation
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Invokes the private constructor of a utility class and verifies it throws UnsupportedOperationException.
     *
     * @param clazz the utility class to test
     * @param <T> the type of the class
     * @throws Exception if an error occurs during reflection
     */
    public static <T> void invokePrivateConstructor(Class<T> clazz) throws Exception {
        MethodHandle constructorHandle = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup())
                .findConstructor(clazz, MethodType.methodType(void.class));
        try {
            constructorHandle.invokeWithArguments();
        } catch (Throwable throwable) {
            if (throwable instanceof InvocationTargetException invocationTargetException) {
                throw invocationTargetException;
            }
            throw new InvocationTargetException(throwable);
        }
    }

    /**
     * Asserts that a class is a proper utility class.
     * 
     * <p>A proper utility class should meet the following criteria:
     * 1. The class must be final
     * 2. It must have a private constructor
     * 3. The constructor must throw UnsupportedOperationException
     *
     * @param clazz the class to test
     * @param <T> the type of the class
     * @throws AssertionError if the class doesn't meet the utility class criteria
     */
    public static <T> void assertUtilityClass(Class<T> clazz) {
        // Check if the class is final
        if (!java.lang.reflect.Modifier.isFinal(clazz.getModifiers())) {
            Assertions.fail(String.format("Utility class %s should be final. Current modifiers: %s", 
                clazz.getSimpleName(), 
                java.lang.reflect.Modifier.toString(clazz.getModifiers())));
        }

        try {
            // Get the constructor and check its visibility
            java.lang.reflect.Constructor<T> constructor = clazz.getDeclaredConstructor();
            
            // Check if the constructor is private
            if (!java.lang.reflect.Modifier.isPrivate(constructor.getModifiers())) {
                Assertions.fail(String.format(
                    "Constructor of utility class %s should be private. Current modifier: %s",
                    clazz.getSimpleName(),
                    java.lang.reflect.Modifier.toString(constructor.getModifiers())));
            }

            MethodHandle constructorHandle = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup())
                    .findConstructor(clazz, MethodType.methodType(void.class));

            try {
                constructorHandle.invokeWithArguments();
                Assertions.fail(String.format(
                    "Constructor of utility class %s should throw UnsupportedOperationException. "
                            + "Successfully created instance of type: %s",
                    clazz.getSimpleName(),
                    clazz.getName()));
            } catch (Throwable throwable) {
                Throwable cause = throwable instanceof InvocationTargetException invocationException
                        ? invocationException.getCause()
                        : throwable;

                if (!(cause instanceof UnsupportedOperationException)) {
                    throw new AssertionError(String.format(
                        "Constructor of utility class %s should throw UnsupportedOperationException, "
                                + "but threw: %s with message: %s",
                        clazz.getSimpleName(),
                        cause.getClass().getName(),
                        cause.getMessage()), cause);
                }
            }
            
        } catch (NoSuchMethodException e) {
            throw new AssertionError(String.format(
                "Utility class %s should have a no-argument constructor. Error: %s",
                clazz.getSimpleName(), e.getMessage()), e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(String.format(
                "Failed to access constructor of utility class %s. Error: %s",
                clazz.getSimpleName(), e.getMessage()), e);
        } catch (Exception e) {
            throw new AssertionError(String.format(
                "Unexpected error while testing utility class %s: %s - %s",
                clazz.getSimpleName(),
                e.getClass().getName(),
                e.getMessage()), e);
        }
    }
}

package run.vexa.reactor.core.test;

import java.lang.reflect.Constructor;

/**
 * Utility class for test helpers.
 */
public final class TestUtils {

    private TestUtils() {
        // Utility class
    }

    /**
     * Utility method to test private constructors that throw UnsupportedOperationException.
     *
     * @param clazz the class with a private constructor to test
     * @throws Exception if there's an error invoking the constructor
     */
    public static <T> void invokePrivateConstructor(Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (Exception e) {
            if (e.getCause() instanceof UnsupportedOperationException) {
                throw (UnsupportedOperationException) e.getCause();
            }
            throw e;
        }
    }
}

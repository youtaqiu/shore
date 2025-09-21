package run.vexa.reactor.commons.function;

/**
 * Customizer is a functional interface for customizing objects of type T.
 * It provides a single method, customize, which is used to apply customizations to an object.
 *
 * @param <T> the type of the object to be customized
 * @author youta
 */
@FunctionalInterface
public interface Customizer<T> {

    /**
     * Customize.
     *
     * @param t the t
     */
    void customize(T t);
}

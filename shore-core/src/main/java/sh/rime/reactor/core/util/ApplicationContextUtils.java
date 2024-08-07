package sh.rime.reactor.core.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * Get bean from ApplicationContext or create instance by reflect.
 *
 * @author rained
 **/
@UtilityClass
@Slf4j
@SuppressWarnings("all")
public class ApplicationContextUtils {

    /**
     * Get bean from ApplicationContext or create instance by reflect.
     * @param context the context
     * @param clazz the clazz
     * @return the bean or reflect
     * @param <T> the type parameter
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanOrReflect(ApplicationContext context, Class<T> clazz) {
        try {
            return context.getBean(clazz);
        } catch (Exception e1) {
            try {
                log.warn("Failed to get bean from applicationContext！", e1);
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e2) {
                log.warn("Failed to create instance by reflect.", e2);
                try {
                    return (T) clazz.getMethod("create").invoke(null);
                } catch (Exception e3) {
                    throw new RuntimeException("Failed to create instance through create static method.", e3);
                }
            }
        }
    }

}

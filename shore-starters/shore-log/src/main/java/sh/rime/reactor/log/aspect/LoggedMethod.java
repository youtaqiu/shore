package sh.rime.reactor.log.aspect;

import java.util.Map;

/**
 * A logged method.
 * This class is used to store information about a method that is being logged.
 * It is used to cache the method signature and the method being logged.
 *
 * @param logContent    The content of the log.
 * @param requestUri    The request URI of the method being logged.
 * @param className     The class name of the method being logged.
 * @param methodName    The name of the method being logged.
 * @param params        The names of the parameters of the method being logged.
 * @param remoteAddr    The remote address of the method being logged.
 * @param queryParamMap The indexes of the parameters of the method being logged that should be included in the log.
 * @author rained
 */
public record LoggedMethod(String logContent, String requestUri, String className, String methodName,
                           Map<String, Object> params, String remoteAddr,
                           Map<String, Object> queryParamMap) {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param logContent    The content of the log.
     * @param requestUri    The request URI of the method being logged.
     * @param className     The class name of the
     * @param methodName    The name of the method being logged.
     * @param params        The parameters of the method being logged.
     * @param remoteAddr    The remote address of the method being logged.
     * @param queryParamMap The query parameters of the method being logged.
     */
    public LoggedMethod {
    }

}

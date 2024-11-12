package sh.rime.reactor.web.exception;

import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.web.properties.GlobalExceptionProperties;
import sh.rime.reactor.web.util.RequestUtil;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

/**
 * WebFluxErrorWebExceptionHandler is a class that handles WebFlux errors.
 * It extends DefaultErrorWebExceptionHandler and overrides the getRoutingFunction and renderErrorResponse methods.
 * This class uses the GlobalExceptionHandler to build the response for the error.
 *
 * @author youta
 */
public class WebFluxErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    private final GlobalExceptionHandler globalExceptionHandler;

    private final GlobalExceptionProperties globalExceptionProperties;

    /**
     * 创建异常处理.
     *
     * @param errorAttributes           ErrorAttributes
     * @param resources                 WebProperties.Resources
     * @param errorProperties           ErrorProperties
     * @param applicationContext        ApplicationContext
     * @param globalExceptionHandler    异常统一处理配置
     * @param globalExceptionProperties 全局异常配置
     */
    public WebFluxErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                                           ErrorProperties errorProperties, ApplicationContext applicationContext,
                                           GlobalExceptionHandler globalExceptionHandler, GlobalExceptionProperties globalExceptionProperties) {
        super(errorAttributes, resources, errorProperties, applicationContext);
        this.globalExceptionHandler = globalExceptionHandler;
        this.globalExceptionProperties = globalExceptionProperties;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        String requestMethod = request.method().name();
        String requestPath = RequestUtil.parseRequestUri(request);
        Throwable error = getError(request);
        Result<?> fail = globalExceptionHandler.build(error, requestMethod, requestPath);
        int httpCode = globalExceptionProperties.getHttpCode();
        if (fail.getCode() < 500) {
            httpCode = fail.getCode();
        }
        return ServerResponse.status(httpCode).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(fail));
    }

}

package run.vexa.reactor.web.autoconfigure;

import run.vexa.reactor.web.exception.GlobalExceptionHandler;
import run.vexa.reactor.web.exception.WebFluxErrorWebExceptionHandler;
import run.vexa.reactor.web.properties.GlobalExceptionProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * WebFluxGlobalExceptionHandlerAutoConfigure is a configuration class that sets up the global exception handling for WebFlux.
 * It includes methods for creating the global exception handler and the exception handler.
 *
 * @author youta
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GlobalExceptionProperties.class)
public class WebFluxGlobalExceptionHandlerAutoConfigure {

    private final ServerProperties serverProperties;

    private final WebProperties.Resources resourceProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    /**
     * Constructor for the WebFluxGlobalExceptionHandlerAutoConfigure class.
     *
     * @param serverProperties      ServerProperties
     * @param webProperties         WebProperties
     * @param viewResolvers         List of ViewResolvers
     * @param serverCodecConfigurer ServerCodecConfigurer
     */
    public WebFluxGlobalExceptionHandlerAutoConfigure(ServerProperties serverProperties, WebProperties webProperties,
                                                      List<ViewResolver> viewResolvers, ServerCodecConfigurer serverCodecConfigurer) {
        this.serverProperties = serverProperties;
        this.resourceProperties = webProperties.getResources();
        this.viewResolvers = Collections.unmodifiableList(viewResolvers);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * Creates the global exception handler configuration.
     *
     * @param globalExceptionProperties GlobalExceptionProperties
     * @return the global exception handler
     */
    @Bean
    public GlobalExceptionHandler webFluxGlobalExceptionHandler(GlobalExceptionProperties globalExceptionProperties) {
        return new GlobalExceptionHandler(globalExceptionProperties);
    }

    /**
     * Creates the exception handler.
     *
     * @param errorAttributes           ErrorAttributes
     * @param applicationContext        ApplicationContext
     * @param globalExceptionHandler    GlobalExceptionHandler
     * @param globalExceptionProperties GlobalExceptionProperties
     * @return the exception handler
     */
    @Order(-2)
    @Bean
    public AbstractErrorWebExceptionHandler webFluxErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                                                            ApplicationContext applicationContext, GlobalExceptionHandler globalExceptionHandler,
                                                                            GlobalExceptionProperties globalExceptionProperties) {
        AbstractErrorWebExceptionHandler exceptionHandler = new WebFluxErrorWebExceptionHandler(errorAttributes,
                resourceProperties, this.serverProperties.getError(), applicationContext, globalExceptionHandler, globalExceptionProperties);
        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

}

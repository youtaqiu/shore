package me.youm.reactor.webflux.handler;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import me.youm.reactor.common.constants.ShoreConstant;
import me.youm.reactor.common.exception.AuthorityException;
import me.youm.reactor.common.exception.IdempotentException;
import me.youm.reactor.common.exception.TokenException;
import me.youm.reactor.common.model.Result;
import me.youm.reactor.common.utils.ResponseUtil;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.nio.charset.StandardCharsets;

/**
 * @author youta
 **/
@Configuration
@Order(-2)
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    @NonNull
    public Mono<Void> handle(ServerWebExchange serverWebExchange, @NonNull Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        if (throwable instanceof TokenException) {
            return ResponseUtil.webFluxResponseWriter(response, ShoreConstant.JSON_UTF8, HttpStatus.UNAUTHORIZED, throwable.getMessage());
        }
        if (throwable instanceof AuthorityException) {
            return ResponseUtil.webFluxResponseWriter(response, ShoreConstant.JSON_UTF8, HttpStatus.FORBIDDEN, throwable.getMessage());
        }
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, ShoreConstant.JSON_UTF8);
        Result<?> result = ExceptionHandlerAdvice.handle(throwable);
        response.setStatusCode(HttpStatus.OK);
        if (throwable instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) throwable).getStatus());
        }
        if (throwable instanceof IdempotentException) {
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }
}

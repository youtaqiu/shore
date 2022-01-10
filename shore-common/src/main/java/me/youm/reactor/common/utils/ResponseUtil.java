package me.youm.reactor.common.utils;

import cn.hutool.json.JSONUtil;
import me.youm.reactor.common.model.Result;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author xiyu
 **/
public class ResponseUtil {

    /**
     * 设置webflux模型响应
     *
     * @param response    ServerHttpResponse
     * @param contentType content-type
     * @param status      http状态码
     * @param value       响应内容
     * @return Mono<Void>
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType,
                                                   HttpStatus status, Object value) {
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
        Result<?> result = Result.error(status.value(), value.toString());
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }

}

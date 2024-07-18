package io.irain.reactor.security.util;

import cn.hutool.core.text.CharPool;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.context.SecurityContextServerWebExchange;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author youta
 **/
public class ReactiveBodyParamUtil {

    private static final String EQ = "=";

    /**
     * 从request中获取body
     *
     * @param request request
     * @return body
     */
    public static String parseBodyFromRequest(ServerHttpRequest request) {
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> atomRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.readableByteCount());
            buffer.toByteBuffer(byteBuffer);
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
            DataBufferUtils.release(buffer);
            atomRef.set(charBuffer.toString());
        });
        return atomRef.get();
    }

    /**
     * 从request中获取body
     *
     * @param request request
     * @return body
     */
    public static Mono<Map<String, String>> getFormBodyMap(ServerRequest request) {
        ServerWebExchange exchange = request.exchange();
        if (exchange instanceof SecurityContextServerWebExchange securityContextServerWebExchange) {
            ServerWebExchange delegate = securityContextServerWebExchange.getDelegate();
            ServerHttpRequest httpRequest = delegate.getRequest();
            String body = parseBodyFromRequest(httpRequest);
            Map<String, String> paramMap = Arrays.stream(body.split(String.valueOf(CharPool.AMP)))
                    .map(pair -> pair.split(EQ))
                    .filter(keyValue -> keyValue.length == 2)
                    .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
            return Mono.just(paramMap);
        } else {
            return request.formData()
                    .map(MultiValueMap::toSingleValueMap);
        }
    }

    /**
     * 从request中获取body
     *
     * @param request request
     * @return body
     */
    public static Mono<String> getBody(ServerRequest request) {
        ServerWebExchange exchange = request.exchange();
        if (exchange instanceof SecurityContextServerWebExchange securityContextServerWebExchange) {
            ServerWebExchange delegate = securityContextServerWebExchange.getDelegate();
            ServerHttpRequest httpRequest = delegate.getRequest();
            String body = parseBodyFromRequest(httpRequest);
            return Mono.just(body);
        } else {
            return request.bodyToMono(String.class);
        }
    }


}

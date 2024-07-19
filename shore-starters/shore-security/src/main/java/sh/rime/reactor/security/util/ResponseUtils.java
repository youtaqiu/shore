package sh.rime.reactor.security.util;

import cn.hutool.json.JSONUtil;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author youta
 **/
public class ResponseUtils {

    /**
     * 构建响应
     * @param response 响应
     * @param data 数据
     * @return Mono
     */
    public static Mono<Void> build(ServerHttpResponse response, Object data) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(
                Mono.just(
                        response.bufferFactory()
                                .wrap(JSONUtil.toJsonStr(data).getBytes(StandardCharsets.UTF_8))));
    }
}

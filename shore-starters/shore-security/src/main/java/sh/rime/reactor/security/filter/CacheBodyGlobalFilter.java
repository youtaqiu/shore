package sh.rime.reactor.security.filter;

import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;

/**
 * CacheBodyGlobalFilter is a WebFilter that caches the body content.
 *
 * @author youta
 **/
public class CacheBodyGlobalFilter implements WebFilter, Ordered {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public CacheBodyGlobalFilter() {
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    /**
     * 把Body体内容放进ServerHttpRequestDecorator中在过滤器链中传递下去
     *
     * @param exchange ServerWebExchange
     * @param chain    WebFilterChain
     * @return mono
     */
    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        final ServerHttpRequest request = exchange.getRequest();
        long contentLength = request.getHeaders().getContentLength();
        var contentType = request.getHeaders().getContentType();
        if (contentType != null && contentType.getType().contains(MediaType.MULTIPART_FORM_DATA.getType())) {
            return chain.filter(exchange);
        }
        if (contentLength <= 0) {
            return chain.filter(exchange);
        }
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        return Mono.create(sink -> DataBufferUtils.write(request.getBody(), outputStream).subscribe(DataBufferUtils::release, sink::error, sink::success))
                .then(Mono.just(request))
                .flatMap(req -> {
                    final ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(req) {
                        @Override
                        @NonNull
                        public Flux<DataBuffer> getBody() {
                            return DataBufferUtils.read(new ByteArrayResource(outputStream.toByteArray()), exchange.getResponse().bufferFactory(), 1024 * 8);
                        }
                    };

                    return chain.filter(exchange.mutate().request(decorator).build());
                });
    }

}

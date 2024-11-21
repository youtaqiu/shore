package sh.rime.reactor.logging.loki;

import com.github.loki4j.client.http.HttpConfig;
import com.github.loki4j.client.http.Loki4jHttpClient;
import com.github.loki4j.client.http.LokiResponse;
import okhttp3.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static com.github.loki4j.client.http.HttpHeader.*;

/**
 * Loki4j OkHttpClient
 *
 * @author youta
 */
public class Loki4jOkHttpClient implements Loki4jHttpClient {
    private final HttpConfig conf;
    private final OkHttpClient httpClient;
    private final MediaType mediaType;
    private final Request requestBuilder;
    private byte[] bodyBuffer = new byte[0];

    /**
     * 构造函数
     *
     * @param conf 配置
     */
    public Loki4jOkHttpClient(HttpConfig conf) {
        this.conf = conf;
        this.httpClient = okHttpClientBuilder(conf);
        this.mediaType = MediaType.get(conf.contentType);
        this.requestBuilder = requestBuilder(conf);
    }

    /**
     * OkHttpClient构建
     *
     * @param conf 配置
     * @return OkHttpClient
     */
    private static OkHttpClient okHttpClientBuilder(HttpConfig conf) {
        return new OkHttpClient.Builder()
                .connectTimeout(conf.connectionTimeoutMs, TimeUnit.MICROSECONDS)
                .writeTimeout(conf.requestTimeoutMs, TimeUnit.MICROSECONDS)
                .readTimeout(conf.requestTimeoutMs, TimeUnit.MICROSECONDS)
                .build();
    }

    /**
     * Request构建
     *
     * @param conf 配置
     * @return Request
     */
    private static Request requestBuilder(HttpConfig conf) {
        Request.Builder request = new Request.Builder()
                .url(conf.pushUrl)
                .addHeader(CONTENT_TYPE, conf.contentType);
        conf.tenantId.ifPresent(tenant -> request.addHeader(X_SCOPE_ORGID, tenant));
        conf.basicAuthToken().ifPresent(token -> request.addHeader(AUTHORIZATION, "Basic " + token));
        return request.build();
    }

    @Override
    public HttpConfig getConfig() {
        return this.conf;
    }

    @Override
    public LokiResponse send(ByteBuffer batch) {
        Request.Builder request = requestBuilder.newBuilder();
        if (batch.hasArray()) {
            request.post(RequestBody.create(batch.array(), mediaType, batch.position(), batch.remaining()));
        } else {
            int len = batch.remaining();
            if (len > bodyBuffer.length) {
                bodyBuffer = new byte[len];
            }
            batch.get(bodyBuffer, 0, len);
            request.post(RequestBody.create(bodyBuffer, mediaType, 0, len));
        }
        Call call = httpClient.newCall(request.build());
        try (Response response = call.execute()) {
            String body = response.body().string();
            return new LokiResponse(response.code(), body);
        } catch (IOException e) {
            throw new IllegalStateException("Error while sending batch to Loki", e);
        }
    }

    @Override
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
        Cache cache = httpClient.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                throw new IllegalStateException("Error while closing OkHttpClient cache", e);
            }
        }
    }
}

package sh.rime.reactor.logging.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ClassUtils;


/**
 * logging 配置
 *
 * @author youta
 */
@Getter
@Setter
@ConfigurationProperties(LoggingProperties.PREFIX)
public class LoggingProperties {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LoggingProperties() {
        // This constructor is intentionally empty.
    }

    /**
     * 日志输出方式
     */
    public static final String PREFIX = "shore.logging";

    private final Console console = new Console();
    private final Files files = new Files();
    private final Loki loki = new Loki();
    private final OpenTelemetry otel = new OpenTelemetry();

    /**
     * 日志输出方式
     */
    @Getter
    @Setter
    public static class Console {

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        public Console() {
            // This constructor is intentionally empty.
        }

        /**
         * 是否启动完成后关闭控制台日志，适用于，正式环境
         */
        private boolean closeAfterStart = false;
    }

    /**
     * 日志输出方式
     */
    @Getter
    @Setter
    public static class OpenTelemetry {

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        public OpenTelemetry() {
            // This constructor is intentionally empty.
        }

        /**
         * 前缀
         */
        public static final String PREFIX = LoggingProperties.PREFIX + ".otel";
        /**
         * 是否开启 OpenTelemetry 日志收集
         */
        private boolean enabled = false;
    }


    /**
     * 文件日志配置
     */
    @Getter
    @Setter
    public static class Files {

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        public Files() {
            // This constructor is intentionally empty.
        }

        /**
         * 日志文件路径，默认： logs
         */
        public static final String PREFIX = LoggingProperties.PREFIX + ".files";
        /**
         * 是否开启文件日志
         */
        private boolean enabled = true;
        /**
         * 使用 json 格式化
         */
        private boolean useJsonFormat = false;
    }


    /**
     * loki 配置
     */
    @Getter
    @Setter
    public static class Loki {
        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        public Loki() {
            // This constructor is intentionally empty.
        }

        /**
         * 日志文件路径，默认： logs
         */
        public static final String PREFIX = LoggingProperties.PREFIX + ".loki";
        /**
         * 是否开启 loki 日志收集
         */
        private boolean enabled = false;
        /**
         * 编码方式，支持 Json、ProtoBuf，默认： Json
         */
        private LokiEncoder encoder = LokiEncoder.JSON;
        /**
         * http sender，支持 java11、OKHttp、ApacheHttp，默认: 从项目依赖中查找，顺序 java11 -> okHttp -> ApacheHttp
         */
        private HttpSender httpSender;
        /**
         * 通用配置
         */
        private int batchMaxItems = 1000;
        private int batchMaxBytes = 4 * 1024 * 1024;
        private long batchTimeoutMs = 60000;
        private long sendQueueMaxBytes = 41943040;
        /**
         * 使用堆外内存
         */
        private boolean useDirectBuffers = true;
        private boolean drainOnStop = true;
        /**
         * 开启 metrics
         */
        private boolean metricsEnabled = false;
        private boolean verbose = false;
        /**
         * http 配置，默认: <a href="http://localhost:3100/loki/api/v1/push">...</a>
         */
        private String httpUrl = "http://localhost:3100/loki/api/v1/push";
        private long httpConnectionTimeoutMs = 30000;
        private long httpRequestTimeoutMs = 5000;
        private String httpAuthUsername;
        private String httpAuthPassword;
        private String httpTenantId;
        /**
         * format 标签，默认： appName=${appName},profile=${profile},host=${HOSTNAME},level=%level,traceId=%X{traceId:-NAN},requestId=%X{requestId:-}
         */
        private String formatLabelPattern = "app=${appName},profile=${profile},host=${HOSTNAME},level=%level,thread=%thread,traceID=%X{trace_id:-NONE}";
        /**
         * format 标签扩展
         */
        private String formatLabelPatternExtend;
        /**
         * format 标签分隔符，默认:，
         */
        private String formatLabelPairSeparator = ",";
        /**
         * format 标签 key、value 分隔符，默认: =
         */
        private String formatLabelKeyValueSeparator = "=";
        /**
         * 消息体格式，默认为: l=%level c=%logger{20} t=%thread | %msg %ex
         */
        private String formatMessagePattern = "level=%level class=%logger{20} thread=%thread traceID=%X{trace_id} | %msg %ex";
        private boolean formatStaticLabels = false;
    }

    /**
     * 编码方式
     */
    public enum LokiEncoder {

        /**
         * Encoder
         */
        JSON,
        /**
         * ProtoBuf
         */
        PROTOBUF
    }

    /**
     * http Sender
     */
    @Getter
    @RequiredArgsConstructor
    public enum HttpSender {
        /**
         * http 方式
         */
        JAVA11("java.net.http.HttpClient"),
        /**
         * okhttp3
         */
        OK_HTTP("okhttp3.OkHttpClient"),
        /**
         * 依赖于 org.apache.httpcomponents:httpclient
         */
        APACHE_HTTP("org.apache.http.impl.client.HttpClients");

        /**
         * sender 判定类
         */
        private final String senderClass;

        /**
         * isAvailable
         *
         * @return boolean
         */
        public boolean isAvailable() {
            return ClassUtils.isPresent(senderClass, null);
        }
    }

}

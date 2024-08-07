package sh.rime.reactor.s3.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 对象存储服务Token.
 *
 * @author youta
 **/
@Data
@Builder
@AllArgsConstructor
public class OssToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public OssToken() {
    }

    /**
     * 对象存储服务的URL
     */
    private String region;

    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKeyId;

    /**
     * Secret key是你账户的密码
     */
    private String accessKeySecret;

    /**
     * Security Token是临时身份凭证，用于临时访问阿里云服务。
     */
    private String stsToken;

    /**
     * bucket名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bucket;

    /**
     * 有效期
     */
    private String expiration;

    /**
     * 文件路径
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;

    /**
     * 自定义域名
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String domain;

}

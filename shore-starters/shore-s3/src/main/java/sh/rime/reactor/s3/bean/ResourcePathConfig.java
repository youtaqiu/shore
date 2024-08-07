package sh.rime.reactor.s3.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 资源路径配置.
 *
 * @author youta
 **/
@Data
@Builder
@AllArgsConstructor
public class ResourcePathConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public ResourcePathConfig() {
    }

    /**
     * 文件路径
     */
    private String path;

    /**
     * 自定义域名
     */
    private String domain;

    /**
     * bucket名称
     */
    private String bucket;
}

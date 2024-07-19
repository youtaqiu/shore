package sh.rime.reactor.s3.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author youta
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourcePathConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

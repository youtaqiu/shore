package run.vexa.reactor.security.domain;

import run.vexa.reactor.commons.enums.GrantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * ClientInfo is a class that represents client information.
 *
 * @author youta
 **/
@Data
@SuperBuilder
@AllArgsConstructor
@Accessors(chain = true)
public class ClientInfo implements Serializable {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public ClientInfo() {
    }

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 授权类型
     */
    private List<GrantType> grantTypes;

    /**
     * 授权范围
     */
    private List<String> scopes;

    /**
     * token过期时间
     */
    @Builder.Default
    private long expire = 3600L;

    /**
     * 刷新token过期时间
     */
    @Builder.Default
    private long refreshExpire = 3600 * 24 * 7L;

    /**
     * 是否自动授权
     */
    private boolean autoApprove;

    /**
     * 并行登录数量
     */
    private int concurrentLoginCount;

    /**
     * 构造基础客户端信息
     *
     * @param clientId     客户端id
     * @param clientSecret 客户端密钥
     * @return 客户端信息
     */
    public static ClientInfo base(String clientId, String clientSecret) {
        return ClientInfo.builder()
                .concurrentLoginCount(1)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}

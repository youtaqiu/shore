package me.youm.reactor.common.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author youta
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Data
public class TokenInfo extends CurrentUser  {

    private String userId;
    private String clientId;
    private String roleId;
    private String roleName;
    private Integer type;
    private String avatar;
    private String nickName;
}

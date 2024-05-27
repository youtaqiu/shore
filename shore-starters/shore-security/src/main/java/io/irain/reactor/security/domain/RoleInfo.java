package io.irain.reactor.security.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author youta
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class RoleInfo implements Serializable {

    /**
     * 角色id
     */
    private RoleEnum role;

    /**
     * 角色名称
     */
    private String roleName;

}


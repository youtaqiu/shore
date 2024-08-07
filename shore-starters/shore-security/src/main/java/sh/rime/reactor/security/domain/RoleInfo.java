package sh.rime.reactor.security.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * RoleInfo is a class that represents role information.
 * @author youta
 **/
@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class RoleInfo implements Serializable {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public RoleInfo() {
    }

    /**
     * 角色id
     */
    private RoleEnum role;

    /**
     * 角色名称
     */
    private String roleName;

}


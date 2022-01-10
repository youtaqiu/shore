package me.youm.reactor.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author youta
 */
@Getter
@AllArgsConstructor
public enum SuperAdmin {

    /**
     * 超级管理员
     */
    ADMIN("admin"),
    ;

    private String value;
}

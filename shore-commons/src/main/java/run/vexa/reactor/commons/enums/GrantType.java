package run.vexa.reactor.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GrantType is an enum that represents different types of authorization grants, such as password, SMS code, authorization code, etc.
 * Each enum value represents a specific grant type.
 *
 * @author youta
 */
@Getter
@AllArgsConstructor
public enum GrantType {

    /**
     * 密码模式
     */
    PASSWORD("password"),

    /**
     * 短信验证码模式
     */
    SMS_CODE("sms_code"),

    /**
     * 授权码模式
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * 客户端模式
     */
    CLIENT_CREDENTIAL("client_credential"),

    /**
     * 刷新token模式
     */
    REFRESH_TOKEN("refresh_token");

    private final String value;
}

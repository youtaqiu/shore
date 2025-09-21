package run.vexa.reactor.commons.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RequestMethodEnum is an enumeration that represents the types of HTTP request methods.
 * It is used in conjunction with the @Anonymous annotation to specify the type of request method for a particular endpoint.
 * The enumeration values include GET, POST, PUT, PATCH, DELETE, and ALL.
 * The aLL value is used as a fallback when no specific request method type matches.
 * <p>
 * Each enumeration value is associated with a string representation of the HTTP request method.
 * The find method is provided to retrieve the enumeration value corresponding to a given string.
 *
 * @author youta
 */
@Getter
@AllArgsConstructor
public enum RequestMethodEnum {

    /**
     * 搜寻 @AnonymousGetMapping
     */
    GET("GET"),

    /**
     * 搜寻 @AnonymousPostMapping
     */
    POST("POST"),

    /**
     * 搜寻 @AnonymousPutMapping
     */
    PUT("PUT"),

    /**
     * 搜寻 @AnonymousPatchMapping
     */
    PATCH("PATCH"),

    /**
     * 搜寻 @AnonymousDeleteMapping
     */
    DELETE("DELETE"),

    /**
     * 否则就是所有 Request 接口都放行
     */
    ALL("All");

    /**
     * Request 类型
     */
    private final String type;

    /**
     * 根据类型查找
     *
     * @param type 类型
     * @return {@link RequestMethodEnum}
     */
    public static RequestMethodEnum find(String type) {
        for (RequestMethodEnum value : RequestMethodEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return ALL;
    }
}

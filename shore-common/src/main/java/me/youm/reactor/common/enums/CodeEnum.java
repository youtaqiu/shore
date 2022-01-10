package me.youm.reactor.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author youta
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    /**
     * 业务异常
     */
    BAD_REQUEST(400, "参数异常"),
    UNAUTHORIZED(401, "请求未授权"),
    TWO_STEP_VALID_FAILURE(402, "二次验证失败"),
    FORBIDDEN(403, "无权访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方式不正确"),
    TWO_STEP_VALID(428, "需要二次验证"),
    /**
     * 服务异常
     */
    ERROR(500, "服务异常"),
    /**
     * Too Many Requests
     */
    TOO_MANY_REQUESTS(429, "Too Many Requests"),

    ;
    /**
     * 状态码
     */
    final int code;
    /**
     * 消息内容
     */
    final String msg;

}

package sh.rime.reactor.commons.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import sh.rime.reactor.commons.constants.CommonConstant;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.commons.exception.ServerFailure;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * Result is a utility class for creating responses in a reactive Spring application.
 * It provides various static methods for creating successful or failed responses, with optional data, status codes, and messages.
 * The class also includes methods for creating responses from Mono or Flux instances.
 * Each response is an instance of the Result class, which includes fields for the status code, message, and data of the response.
 * The Result class implements Serializable, allowing its instances to be serialized for transmission over the network.
 *
 * @param <T> The type of data that this class can handle.
 * @author youta
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 129486345465L;

    /**
     * 返回码
     */
    @Schema(description = "返回码")
    private Integer code;

    /**
     * 返回描述
     */
    @Schema(description = "返回描述")
    private String message;

    /**
     * 异常
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "异常信息")
    private String exception;

    /**
     * 数据载体
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "数据载体")
    private T data;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public Result() {
    }


    /**
     * 成功
     *
     * @param <T> 泛型
     * @return Result
     */
    public static <T> Result<T> ok() {
        return Result.<T>builder()
                .message(CommonConstant.SUCCESS_MSG)
                .code(CommonConstant.SUCCESS_CODE)
                .build();
    }

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  泛型
     * @return Result
     */
    public static <T> Result<T> ok(T data) {
        return Result.<T>builder()
                .message(CommonConstant.SUCCESS_MSG)
                .code(CommonConstant.SUCCESS_CODE)
                .data(data)
                .build();
    }

    /**
     * 失败
     *
     * @return Result
     */
    public static Result<Void> failed() {
        return Result.<Void>builder()
                .message(ServerException.DEFAULT_MSG)
                .code(CommonConstant.ERROR_CODE)
                .build();
    }

    /**
     * 失败
     *
     * @param msg 错误信息
     * @return Result
     */
    public static Result<Void> failed(String msg) {
        return failed()
                .setMessage(msg);
    }

    /**
     * 失败
     *
     * @param msg       错误信息
     * @param exception 异常
     * @return Result
     */
    public static Result<Void> failed(String msg, String exception) {
        return failed()
                .setException(exception)
                .setMessage(msg);
    }

    /**
     * 失败
     *
     * @param code 错误码
     * @param msg  错误信息
     * @return Result
     */
    public static Result<Void> failed(Integer code, String msg) {
        return failed(msg)
                .setCode(code);
    }

    /**
     * 失败
     *
     * @param code 错误码
     * @param msg  错误信息
     * @param data 数据
     * @param <T>  T
     * @return Result
     */
    public static <T> Result<T> failed(Integer code, String msg, T data) {
        return Result.<T>builder()
                .message(msg)
                .code(code)
                .data(data)
                .build();
    }

    /**
     * 失败
     *
     * @param failure ApplicationFailure
     * @return Result
     */
    public static Result<Void> failed(ServerFailure failure) {
        return failed(failure.code(), failure.message());
    }

    /**
     * 检查结果是否成功
     *
     * @return Result
     */
    public Result<T> check() {
        if (!this.code.equals(CommonConstant.SUCCESS_CODE)) {
            throw new ServerException(this.message, this.code);
        }
        return this;
    }

    /**
     * 检查结果是否成功并返回数据
     *
     * @return T
     */
    public T checkData() {
        return check().getData();
    }

}

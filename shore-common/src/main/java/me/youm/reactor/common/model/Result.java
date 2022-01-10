package me.youm.reactor.common.model;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;
import me.youm.reactor.common.enums.CodeEnum;
import me.youm.reactor.common.enums.EnumInterface;
import me.youm.reactor.common.constants.ResultConstant;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author youta
 */
@ToString
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 129486345465L;

    private Boolean success;
    private Integer code;
    private String message;
    private long timeStamp = System.currentTimeMillis();
    private String requestId = IdUtil.objectId();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public Result() {
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isOk(@NonNull Result<?> result) {
        return Objects.requireNonNull(result).isSuccess();
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isNotOk( Result<?> result) {
        return !Result.isOk(result);
    }

    public Result(Boolean success, String message, Integer code, long timeStamp, String requestId, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.timeStamp = timeStamp;
        this.requestId = requestId;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        Result<T> responseData = new Result<T>();
        responseData.setSuccess(true);
        responseData.setMessage(ResultConstant.SUCCESS_MSG);
        responseData.setCode(ResultConstant.SUCCESS_CODE);
        return responseData;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> responseData = new Result<T>();
        responseData.setSuccess(true);
        responseData.setMessage(ResultConstant.SUCCESS_MSG);
        responseData.setCode(ResultConstant.SUCCESS_CODE);
        responseData.setData(data);
        return responseData;
    }

    public static Result<?> error() {
        Result<?> responseData = new Result<>();
        responseData.setSuccess(false);
        responseData.setMessage(ResultConstant.ERROR_MSG);
        responseData.setCode(ResultConstant.ERROR_CODE);
        return responseData;
    }

    public static Result<?> error(CodeEnum codeEnum) {
        Result<?> responseData = new Result<>();
        responseData.setSuccess(false);
        responseData.setMessage(codeEnum.getMsg());
        responseData.setCode(codeEnum.getCode());
        return responseData;
    }

    public static Result<?> error(String msg) {
        Result<?> responseData = new Result<>();
        responseData.setSuccess(false);
        responseData.setMessage(msg);
        responseData.setCode(ResultConstant.ERROR_CODE);
        return responseData;
    }

    public static Result<?> error(Integer code, String msg) {
        Result<?> responseData = new Result<>();
        responseData.setSuccess(false);
        responseData.setMessage(msg);
        responseData.setCode(code);
        return responseData;
    }

    public static Result<?> error(EnumInterface failure) {
        Result<?> responseData = new Result<>();
        responseData.setSuccess(false);
        responseData.setCode(failure.getCode());
        responseData.setMessage(failure.getMsg());
        return responseData;
    }

    public Boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public Integer getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public Result<T> setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result<T> setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public Result<T> setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }
}


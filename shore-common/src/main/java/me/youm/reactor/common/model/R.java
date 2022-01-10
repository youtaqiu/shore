package me.youm.reactor.common.model;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;
import me.youm.reactor.common.constants.ResultConstant;
import me.youm.reactor.common.enums.EnumInterface;
import me.youm.reactor.common.exception.BusinessException;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * @author xiyu
 **/
@ToString
public class R<T> implements Serializable {

    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 响应编码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;

    private long timeStamp = System.currentTimeMillis();

    private String requestId = IdUtil.objectId();


    /**
     * 承载数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> Mono<T> error(EnumInterface exceptionEnum) {
        return Mono.error(new BusinessException(exceptionEnum));
    }


    public static <T> Mono<R<T>> ok(Mono<T> monoBody) {
        return monoResponseCreate(monoBody, ResultConstant.SUCCESS_CODE, ResultConstant.SUCCESS_MSG, true);
    }

    public static <T> Mono<R<T>> ok(Mono<T> monoBody, String msg) {
        return monoResponseCreate(monoBody, ResultConstant.SUCCESS_CODE, msg, true);
    }

    public static <T> Mono<R<T>> ok(Mono<T> monoBody, int code, String msg) {
        return monoResponseCreate(monoBody, code, msg, true);
    }

    public static <T> Mono<R<T>> failed(Mono<T> monoBody) {
        return monoResponseCreate(monoBody, ResultConstant.ERROR_CODE, ResultConstant.ERROR_MSG, false);
    }

    public static <T> Mono<R<T>> failed(Mono<T> monoBody, String msg) {
        return monoResponseCreate(monoBody, ResultConstant.ERROR_CODE, msg, false);
    }

    public static <T> Mono<R<T>> failed(Mono<T> monoBody, int code, String msg) {
        return monoResponseCreate(monoBody, code, msg, false);
    }

    public static <T> Mono<R<T>> ok() {
        return responseCreate(ResultConstant.SUCCESS_CODE, ResultConstant.SUCCESS_MSG, true);
    }

    public static <T> Mono<R<T>> failed() {
        return responseCreate(ResultConstant.ERROR_CODE, ResultConstant.ERROR_MSG, false);
    }

    public static <T> Mono<R<T>> ok(T data) {
        return responseCreate(data, ResultConstant.SUCCESS_CODE, ResultConstant.SUCCESS_MSG, true);
    }

    public static <T> Mono<R<T>> ok(T data, String msg) {
        return responseCreate(data, ResultConstant.SUCCESS_CODE, msg, true);
    }

    public static <T> Mono<R<T>> ok(int code, String msg) {
        return responseCreate(code, msg, true);
    }

    public static <T> Mono<R<T>> ok(T data, int code, String msg) {
        return responseCreate(data, code, msg, true);
    }

    public static <T> Mono<R<T>> failed(T data) {
        return responseCreate(data, ResultConstant.ERROR_CODE, ResultConstant.ERROR_MSG, false);
    }

    public static <T> Mono<R<T>> failed(T data, String msg) {
        return responseCreate(data, ResultConstant.ERROR_CODE, msg, false);
    }

    public static <T> Mono<R<T>> failed(int code, String msg) {
        return responseCreate(code, msg, false);
    }

    public static <T> Mono<R<T>> failed(T data, int code, String msg) {
        return responseCreate(data, code, msg, false);
    }

    private static <T> Mono<R<T>> responseCreate(int code, String msg, Boolean success) {
        final R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(success);
        return Mono.just(r);
    }

    private static <T> Mono<R<T>> responseCreate(T data, int code, String msg, Boolean success) {
        Mono<T> monoData = Mono.just(data);
        return getMono(code, msg, success, monoData);
    }

    private static <T> Mono<R<T>> monoResponseCreate(Mono<T> monoData, int code, String msg, Boolean success) {
        return getMono(code, msg, success, monoData);
    }

    private static <T> Mono<R<T>> getMono(int code, String msg, Boolean success, Mono<T> monoData) {
        return monoData.map(x -> {
            final R<T> r = new R<>();
            r.setCode(code);
            r.setData(x);
            r.setMessage(msg);
            r.setSuccess(success);
            return r;
        }).switchIfEmpty(
                R.ok(code, msg));
    }



    public Boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public R<T> setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public R<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }

    public R<T> setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public R<T> setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getRequestId() {
        return requestId;
    }
}

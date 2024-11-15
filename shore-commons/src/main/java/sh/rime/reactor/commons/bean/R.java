package sh.rime.reactor.commons.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import sh.rime.reactor.commons.constants.CommonConstant;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.commons.exception.ServerFailure;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

/**
 * R is a utility class for creating Mono responses in a reactive Spring application.
 * It provides various static methods for creating successful or failed responses, with optional data,
 * status codes, and messages.
 * The class also includes methods for creating responses from Mono or Flux instances.
 * Each response is an instance of the R class, which includes fields for the status code, message,
 * and data of the response.
 * The R class implements Serializable, allowing its instances to be serialized for transmission over the network.
 *
 * @param <T> The type of data that this class can handle.
 * @author youta
 */
@Getter
@ToString
@SuppressWarnings("unused")
public class R<T> implements Serializable {

    /**
     * 响应编码.
     * get code
     */
    @Schema(description = "返回码")
    private Integer code;
    /**
     * 响应信息.
     * get message
     */
    @Schema(description = "返回描述")
    private String message;

    /**
     * 承载数据.
     * get data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "数据载体")
    private T data;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public R() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 响应成功.
     *
     * @param monoBody 响应体
     * @param <T>      响应体类型
     * @return 响应体
     */
    public static <T> Mono<R<T>> ok(Mono<T> monoBody) {
        return monoResponseCreate(monoBody, CommonConstant.SUCCESS_CODE, CommonConstant.SUCCESS_MSG);
    }

    /**
     * 响应成功.
     *
     * @param fluxBody 响应体
     * @param <T>      响应体类型
     * @return 响应体
     */
    public static <T> Mono<R<List<T>>> ok(Flux<T> fluxBody) {
        return fluxResponseCreate(fluxBody, CommonConstant.SUCCESS_CODE, CommonConstant.SUCCESS_MSG);
    }

    /**
     * 响应成功.
     *
     * @param monoBody 响应体
     * @param msg      响应信息
     * @param <T>      响应体类型
     * @return 响应体
     */
    public static <T> Mono<R<T>> ok(Mono<T> monoBody, String msg) {
        return monoResponseCreate(monoBody, CommonConstant.SUCCESS_CODE, msg);
    }

    /**
     * 响应成功.
     *
     * @param fluxBody 响应体
     * @param msg      响应信息
     * @param <T>      响应体类型
     * @return 响应体
     */
    public static <T> Mono<R<List<T>>> ok(Flux<T> fluxBody, String msg) {
        return fluxResponseCreate(fluxBody, CommonConstant.SUCCESS_CODE, msg);
    }

    /**
     * 响应成功.
     *
     * @param monoBody 响应体
     * @param code     响应编码
     * @param msg      响应信息
     * @param <T>      响应体类型
     * @return 响应体
     */
    public static <T> Mono<R<T>> ok(Mono<T> monoBody, int code, String msg) {
        return monoResponseCreate(monoBody, code, msg);
    }

    /**
     * 响应成功.
     *
     * @param fluxBody 响应体
     * @param code     响应编码
     * @param msg      响应信息
     * @param <T>      响应体类型
     * @return 响应体
     */
    public static <T> Mono<R<List<T>>> ok(Flux<T> fluxBody, int code, String msg) {
        return fluxResponseCreate(fluxBody, code, msg);
    }

    /**
     * build error mono
     *
     * @param <T> type
     * @return mono
     */
    public static <T> Mono<R<T>> ok() {
        return responseCreate(CommonConstant.SUCCESS_CODE, CommonConstant.SUCCESS_MSG);
    }

    /**
     * build error mono
     *
     * @param data data
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> ok(T data) {
        return responseCreate(data, CommonConstant.SUCCESS_CODE, CommonConstant.SUCCESS_MSG);
    }

    /**
     * build error mono
     *
     * @param data data
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> ok(T data, String msg) {
        return responseCreate(data, CommonConstant.SUCCESS_CODE, msg);
    }

    /**
     * build error mono
     *
     * @param code code
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> ok(int code, String msg) {
        return responseCreate(code, msg);
    }

    /**
     * build error mono
     *
     * @param data data
     * @param code code
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> ok(T data, int code, String msg) {
        return responseCreate(data, code, msg);
    }

    /**
     * 响应异常.
     *
     * @param throwable 异常
     * @param <T>       响应体类型
     * @return 响应体
     */
    public static <T> Mono<T> error(Throwable throwable) {
        return Mono.error(throwable);
    }

    /**
     * build error mono.
     *
     * @param throwable error
     * @param <T>       type
     * @return mono
     */
    public static <T> Mono<T> error(ServerException throwable) {
        return Mono.error(throwable);
    }

    /**
     * build error mono.
     *
     * @param failure error
     * @param <T>     type
     * @return mono
     */
    public static <T> Mono<T> error(ServerFailure failure) {
        return Mono.error(new ServerException(failure));
    }

    /**
     * 响应失败.
     *
     * @param monoBody 响应体
     * @param <T>      响应体类型
     * @return 响应体
     */
    public static <T> Mono<R<T>> failed(Mono<T> monoBody) {
        return monoResponseCreate(monoBody, CommonConstant.ERROR_CODE, ServerException.DEFAULT_MSG);
    }


    /**
     * build error mono.
     *
     * @param fluxBody flux
     * @param <T>      type
     * @return mono
     */
    public static <T> Mono<R<List<T>>> failed(Flux<T> fluxBody) {
        return fluxResponseCreate(fluxBody, CommonConstant.ERROR_CODE, ServerException.DEFAULT_MSG);
    }

    /**
     * build error mono
     *
     * @param monoBody mono
     * @param msg      msg
     * @param <T>      type
     * @return mono
     */
    public static <T> Mono<R<T>> failed(Mono<T> monoBody, String msg) {
        return monoResponseCreate(monoBody, CommonConstant.ERROR_CODE, msg);
    }

    /**
     * build error mono
     *
     * @param fluxBody flux
     * @param msg      msg
     * @param <T>      type
     * @return mono
     */
    public static <T> Mono<R<List<T>>> failed(Flux<T> fluxBody, String msg) {
        return fluxResponseCreate(fluxBody, CommonConstant.ERROR_CODE, msg);
    }

    /**
     * build error mono
     *
     * @param monoBody mono
     * @param code     code
     * @param msg      msg
     * @param <T>      type
     * @return mono
     */
    public static <T> Mono<R<T>> failed(Mono<T> monoBody, int code, String msg) {
        return monoResponseCreate(monoBody, code, msg);
    }

    /**
     * build error mono
     *
     * @param fluxBody flux
     * @param code     code
     * @param msg      msg
     * @param <T>      type
     * @return mono
     */
    public static <T> Mono<R<List<T>>> failed(Flux<T> fluxBody, int code, String msg) {
        return fluxResponseCreate(fluxBody, code, msg);
    }


    /**
     * build error mono
     *
     * @param <T> type
     * @return mono
     */
    public static <T> Mono<R<T>> failed() {
        return responseCreate(CommonConstant.ERROR_CODE, ServerException.DEFAULT_MSG);
    }


    /**
     * build error mono
     *
     * @param data data
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> failed(T data) {
        return responseCreate(data, CommonConstant.ERROR_CODE, ServerException.DEFAULT_MSG);
    }

    /**
     * build error mono
     *
     * @param data data
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> failed(T data, String msg) {
        return responseCreate(data, CommonConstant.ERROR_CODE, msg);
    }

    /**
     * build error mono
     *
     * @param code code
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> failed(int code, String msg) {
        return responseCreate(code, msg);
    }

    /**
     * build error mono
     *
     * @param data data
     * @param code code
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> failed(T data, int code, String msg) {
        return responseCreate(data, code, msg);
    }

    /**
     * build error mono
     *
     * @param code code
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    private static <T> Mono<R<T>> responseCreate(int code, String msg) {
        final R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(msg);
        return Mono.just(r);
    }

    /**
     * build error mono
     *
     * @param data data
     * @param code code
     * @param msg  msg
     * @param <T>  type
     * @return mono
     */
    public static <T> Mono<R<T>> responseCreate(T data, int code, String msg) {
        Mono<T> monoData = Mono.just(data);
        return getMono(code, msg, monoData);
    }

    /**
     * build error mono
     *
     * @param monoData mono
     * @param code     code
     * @param msg      msg
     * @param <T>      type
     * @return mono
     */
    private static <T> Mono<R<T>> monoResponseCreate(Mono<T> monoData, int code, String msg) {
        return getMono(code, msg, monoData);
    }

    /**
     * build error mono
     *
     * @param fluxData flux
     * @param code     code
     * @param msg      msg
     * @param <T>      type
     * @return mono
     */
    private static <T> Mono<R<List<T>>> fluxResponseCreate(Flux<T> fluxData, int code, String msg) {
        return getFlux(code, msg, fluxData);
    }

    /**
     * build error mono
     *
     * @param code     code
     * @param msg      msg
     * @param monoData mono
     * @param <T>      type
     * @return mono
     */
    private static <T> Mono<R<T>> getMono(int code, String msg, Mono<T> monoData) {
        return monoData
                .map(x -> getTr(code, msg, x))
                .switchIfEmpty(R.ok(code, msg));
    }

    /**
     * build error mono
     *
     * @param code code
     * @param msg  msg
     * @param x    data
     * @param <T>  type
     * @return mono
     */
    private static <T> R<T> getTr(int code, String msg, T x) {
        final R<T> r = new R<>();
        r.setCode(code);
        r.setData(x);
        r.setMessage(msg);
        return r;
    }

    /**
     * build error mono
     *
     * @param code     code
     * @param msg      msg
     * @param fluxData flux
     * @param <T>      type
     * @return mono
     */
    private static <T> Mono<R<List<T>>> getFlux(int code, String msg, Flux<T> fluxData) {
        return fluxData
                .collectList()
                .map(x -> getListR(code, msg, x))
                .switchIfEmpty(R.ok(code, msg));
    }

    /**
     * build error mono
     *
     * @param code code
     * @param msg  msg
     * @param x    data
     * @param <T>  type
     * @return mono
     */
    private static <T> R<List<T>> getListR(int code, String msg, List<T> x) {
        final R<List<T>> r = new R<>();
        r.setCode(code);
        r.setData(x);
        r.setMessage(msg);
        return r;
    }


    /**
     * set message
     *
     * @param message message
     * @return R
     */
    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * set code
     *
     * @param code code
     * @return R
     */
    @SuppressWarnings("all")
    public R<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * set data
     *
     * @param data data
     * @return R
     */
    public R<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return CommonConstant.SUCCESS_CODE.equals(this.code);
    }
}

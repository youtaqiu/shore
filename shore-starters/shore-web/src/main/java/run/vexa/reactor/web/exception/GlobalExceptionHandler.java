package run.vexa.reactor.web.exception;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import run.vexa.reactor.commons.bean.Result;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.web.properties.GlobalExceptionProperties;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * GlobalExceptionHandler is a class that handles all exceptions thrown during the execution of the application.
 * It includes methods for handling different types of exceptions and building the response to be returned to the client.
 * This class uses the @ExceptionHandler annotation to define methods that handle specific types of exceptions.
 *
 * @author youta
 */
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 全局异常配置
     */
    private final GlobalExceptionProperties globalExceptionProperties;

    /**
     * 构造函数
     *
     * @param globalExceptionProperties 全局异常配置
     */
    public GlobalExceptionHandler(GlobalExceptionProperties globalExceptionProperties) {
        this.globalExceptionProperties = globalExceptionProperties;
    }

    /**
     * 异常处理.
     *
     * @param e                 异常
     * @param serverWebExchange 服务端请求和响应的交互
     * @return 响应数据
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Result<?>> exceptionHandler0(ServerWebExchange serverWebExchange, Throwable e) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String method = request.getMethod().name();
        String path = request.getPath().value();
        Result<?> result = build(e, method, path);
        int httpCode = globalExceptionProperties.getHttpCode();
        if (result.getCode() < 500) {
            httpCode = result.getCode();
        }
        return ResponseEntity.status(httpCode).body(result);
    }

    /**
     * 构建响应数据
     *
     * @param e      异常
     * @param method 请求方法
     * @param path   请求路径
     * @return 响应数据
     */
    public Result<?> build(Throwable e, String method, String path) {
        log.debug("request failed", e);
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        Result<Void> fail = Result.failed();
        String defaultMsg = "The system is busy. Please try again later.";
        if (responseStatus != null) {
            int errorCode = responseStatus.value().value();
            String message = responseStatus.reason();
            fail = Result.failed(errorCode, message);
        } else if (StrUtil.isBlank(e.getMessage())) {
            fail = Result.failed(500, defaultMsg);
        } else {
            switch (e) {
                case MethodArgumentNotValidException validException -> {
                    BindingResult bindingResult = validException.getBindingResult();
                    List<ObjectError> errors = bindingResult.getAllErrors();
                    StringBuffer errorMsg = new StringBuffer();
                    errors.stream()
                            .map(this::getMethodArgumentNotValidExceptionDisplayMessage)
                            .forEach(x -> errorMsg.append(x).append(";"));
                    if (errorMsg.toString().endsWith(";")) {
                        errorMsg.deleteCharAt(errorMsg.length() - 1);
                    }
                    return Result.failed(400, errorMsg.toString());
                }
                case WebExchangeBindException bindException -> {
                    List<FieldError> fieldErrors = bindException.getFieldErrors();
                    String message = String.format("%s", fieldErrors.stream()
                            .map(FieldError::getDefaultMessage)
                            .map(this::getArgumentNotValidExceptionMessage)
                            .collect(Collectors.joining(";")));
                    return Result.failed(400, message);
                }
                case ResponseStatusException ignored -> {
                    String msg = "Invalid request address[%s %s]";
                    return Result.failed(404, String.format(msg, method, path));
                }
                default -> {
                }
            }
            fail = serverException(e, fail);
            log.error("Request failed", e);
            fail.setException(e.getLocalizedMessage());
        }
        if (!globalExceptionProperties.getEnable()) {
            fail.setException(null);
        }
        return fail;
    }

    /**
     * 获取参数校验异常信息
     *
     * @param error 错误信息
     * @return 参数校验异常信息
     */
    private String getMethodArgumentNotValidExceptionDisplayMessage(ObjectError error) {
        String defaultMessage = error.getDefaultMessage();
        if (defaultMessage == null) {
            return error.getDefaultMessage();
        }
        try {
            return defaultMessage;
        } catch (NoSuchMessageException e) {
            return error.getDefaultMessage();
        }
    }

    /**
     * 服务端异常处理
     *
     * @param cause 异常
     * @param fail  失败结果
     * @return 失败结果
     */
    private Result<Void> serverException(Throwable cause, Result<Void> fail) {
        if (cause instanceof ServerException exception) {
            Throwable remoteEx = exception.getCause();
            fail = Result.failed(exception.getErrorCode(), exception.getLocalizedMessage());
            if (remoteEx instanceof RemoteException remoteException) {
                fail.setException(remoteException.getLocalizedMessage());
            }
            return fail;
        }
        return fail;
    }

    /**
     * 获取参数校验异常信息
     *
     * @param errorMessage 错误信息
     * @return 参数校验异常信息
     */
    private String getArgumentNotValidExceptionMessage(String errorMessage) {
        if (errorMessage == null) {
            return null;
        }
        try {
            return errorMessage;
        } catch (NoSuchMessageException e) {
            return errorMessage;
        }
    }

}

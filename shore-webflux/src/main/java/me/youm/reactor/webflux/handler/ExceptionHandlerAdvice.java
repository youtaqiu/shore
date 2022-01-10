package me.youm.reactor.webflux.handler;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import me.youm.reactor.common.enums.CodeEnum;
import me.youm.reactor.common.enums.CommonExceptionEnum;
import me.youm.reactor.common.exception.*;
import me.youm.reactor.common.model.Result;
import me.youm.reactor.common.utils.StringPool;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author youta
 **/
@Slf4j
public class ExceptionHandlerAdvice {

    public static Result<?> handle(ResponseStatusException ex) {
        if (ex instanceof WebExchangeBindException) {
            WebExchangeBindException e = (WebExchangeBindException) ex;
            return handle(e);
        }
        log.error("response status exception", ex);
        if (StrUtil.contains(ex.getMessage(), HttpStatus.NOT_FOUND.toString())) {
            return Result.error(CodeEnum.NOT_FOUND.getCode(), "资源不存在");
        } else if (StrUtil.contains(ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED.toString())) {
            return Result.error(CodeEnum.METHOD_NOT_ALLOWED.getCode(), "请求方式不正确");
        } else if (StrUtil.contains(ex.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString())) {
            return Result.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "不支持的媒体类型");
        } else if (StrUtil.contains(ex.getMessage(), HttpStatus.BAD_REQUEST.toString())) {
            if (StringUtils.hasText(ex.getReason()) && ex.getReason().split(StringPool.COLON).length > 0 && ex.getReason().split(StringPool.COLON)[0] !=null) {
                return Result.error(CommonExceptionEnum.PARAM_ERROR.getCode(), ex.getReason().split(StringPool.COLON)[0]);
            } else {
                return Result.error(CodeEnum.BAD_REQUEST.getCode(), "参数错误");
            }
        } else {
            return Result.error();
        }
    }

    public static Result<?> handle(ConnectTimeoutException ex) {
        log.error("connect timeout exception", ex);
        return Result.error();
    }

    public static Result<?> handle(WebExchangeBindException ex) {
        log.error("param bind exception", ex);
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("参数校验失败:");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append("[").append(fieldError.getField()).append("]").append(":").append(fieldError.getDefaultMessage()).append(";");
        }
        return Result.error(CodeEnum.BAD_REQUEST.getCode(), errorMessage.toString());
    }


    public static Result<?> handle(BusinessException ex) {
        log.error("business exception", ex);
        return Result.error(ex.getCode(), ex.getMsg());
    }

    public static Result<?> handle(AliOssException ex) {
        log.error("ali oss exception", ex);
        return Result.error(ex.getCode(), ex.getMsg());
    }


    public static Result<?> handle(PayException ex) {
        log.error("pay exception", ex);
        return Result.error(ex.getCode(), ex.getMsg());
    }

    public static Result<?> handle(RocketMqException ex) {
        log.error("rocket exception", ex);
        return Result.error(ex.getCode(), ex.getMsg());
    }

    public static Result<?> handle(RuntimeException ex) {
        log.error("runtime exception", ex);
        return Result.error();
    }

    public static Result<?> handle(IdempotentException ex) {
        log.error("idempotent exception", ex);
        return Result.error(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage());
    }

    public Result<?> handleException(RedissonLockException ex) {
        log.error("redisson lock error", ex);
        return Result.error("请求过快");
    }

    public static Result<?> handle(TokenException ex) {
        return Result.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }


    public static Result<?> handle(AuthorityException ex) {
        return Result.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }


    public static Result<?> handle(Exception ex) {
        log.error("exception", ex);
        return Result.error();
    }

//    public static Result<?> handle(CustomRSocketException ex) {
//        log.error("rsocket server exception", ex);
//        return Result.error(ex.errorCode(), ex.getMessage());
//    }

//    public static Result<?> handle(ApplicationErrorException ex) {
//        log.error("rsocket server exception", ex);
//        return Result.error(ex.errorCode(), ex.getMessage());
//    }

//    public static Result<?> handle(RSocketServerException ex) {
//        log.error("rsocket remote server exception", ex);
//        return Result.error(ex.errorCode(), "Internal Server Error");
//    }

//    public static Result<?> handle(InvalidException ex) {
//        log.error("RSocket Service exception", ex);
//        String message = ex.getMessage();
//        if (message.contains("RST-900500")){
//            return Result.error(RSOCKET_SERVER_INVOKED_ERROR);
//        }
//        if (message.contains("RST-900404")){
//            return Result.error(RSOCKET_SERVER_NOT_FOUND_ERROR);
//        }
//        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");
//    }

    public static Result<?> handle(Throwable throwable) {
        Result<?> result = Result.error();
        if (throwable instanceof ResponseStatusException) {
            result = handle((ResponseStatusException) throwable);
        } else if (throwable instanceof ConnectTimeoutException) {
            result = handle((ConnectTimeoutException) throwable);
//        } else if (throwable instanceof CustomRSocketException) {
//            result = handle((CustomRSocketException) throwable);
        } else if (throwable instanceof PayException) {
            result = handle((PayException) throwable);
        } else if (throwable instanceof RocketMqException) {
            result = handle((RocketMqException) throwable);
        } else if (throwable instanceof IdempotentException) {
            result = handle((IdempotentException) throwable);
        } else if (throwable instanceof RedissonLockException) {
            result = handle((RedissonLockException) throwable);
        } else if (throwable instanceof TokenException) {
            result = handle((TokenException) throwable);
        } else if (throwable instanceof AuthorityException) {
            result = handle((AuthorityException) throwable);
        } else if (throwable instanceof BusinessException) {
            result = handle((BusinessException) throwable);
        } else if (throwable instanceof AliOssException) {
            result = handle((AliOssException) throwable);
//        } else if (throwable instanceof InvalidException) {
//            result = handle((InvalidException) throwable);
//        } else if (throwable instanceof ApplicationErrorException) {
//            result = handle((ApplicationErrorException) throwable);
        } else if (throwable instanceof RuntimeException) {
            result = handle((RuntimeException) throwable);
        } else if (throwable instanceof Exception) {
            result = handle((Exception) throwable);
        }
        return result;
    }

}

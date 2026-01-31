package run.vexa.reactor.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.security.domain.LoginRequest;
import run.vexa.reactor.security.grant.AuthenticationGrantManager;

import java.io.IOException;

import static run.vexa.reactor.commons.enums.CommonExceptionEnum.LOGIN_BODY_PARSE_ERROR;


/**
 * PostLoginAuthConverter is a class that represents the converter of post login authentication.
 *
 * @author youta
 **/
@Slf4j
@Component
public class ReactiveServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final ObjectMapper objectMapper;
    private final AuthenticationGrantManager authenticationGrantManager;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param objectMapper               the object mapper
     * @param authenticationGrantManager the authentication grant manager
     */
    public ReactiveServerAuthenticationConverter(ObjectMapper objectMapper, AuthenticationGrantManager authenticationGrantManager) {
        this.objectMapper = objectMapper;
        this.authenticationGrantManager = authenticationGrantManager;
    }

    @Override
    @NullMarked
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        // 读取请求体
        return exchange
                .getRequest()
                .getBody()
                .next()
                .switchIfEmpty(Mono.error(new ServerWebInputException("No body in request")))
                .flatMap(this::getTokenAuthentication);
    }

    /**
     * 获取token认证
     *
     * @param body {@link DataBuffer}
     * @return {@link Authentication}
     */
    @NullMarked
    private Mono<Authentication> getTokenAuthentication(@Nullable DataBuffer body) {
        if (body == null) {
            return Mono.error(new ServerException(LOGIN_BODY_PARSE_ERROR));
        }
        try {
            var loginRequest = objectMapper.readValue(body.asInputStream(), LoginRequest.class);
            return this.authenticationGrantManager.grant(loginRequest.getType(), client -> client.authentication(loginRequest));
        } catch (IOException e) {
            log.error("LoginRequest parse error", e);
            return Mono.error(new ServerException(LOGIN_BODY_PARSE_ERROR));
        }
    }
}

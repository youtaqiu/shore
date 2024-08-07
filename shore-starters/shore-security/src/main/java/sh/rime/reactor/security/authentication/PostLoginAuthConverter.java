package sh.rime.reactor.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.security.domain.LoginRequest;
import sh.rime.reactor.security.grant.AuthenticationGrantManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static sh.rime.reactor.commons.enums.CommonExceptionEnum.LOGIN_BODY_PARSE_ERROR;


/**
 * PostLoginAuthConverter is a class that represents the converter of post login authentication.
 *
 * @author youta
 **/
@Slf4j
@Component
public class PostLoginAuthConverter implements ServerAuthenticationConverter {

    private final ObjectMapper objectMapper;
    private final AuthenticationGrantManager authenticationGrantManager;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param objectMapper               the object mapper
     * @param authenticationGrantManager the authentication grant manager
     */
    public PostLoginAuthConverter(ObjectMapper objectMapper, AuthenticationGrantManager authenticationGrantManager) {
        this.objectMapper = objectMapper;
        this.authenticationGrantManager = authenticationGrantManager;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        // 读取请求体
        return exchange
                .getRequest()
                .getBody()
                .next()
                .switchIfEmpty(Mono.error(new ServerWebInputException("No body in request")))
                .flatMap(this::getTokenAuthentication);
    }

    private Mono<Authentication> getTokenAuthentication(@NonNull DataBuffer body) {
        try {
            var loginRequest = objectMapper.readValue(body.asInputStream(), LoginRequest.class);
            return this.authenticationGrantManager.grant(loginRequest.getType(), client -> client.authentication(loginRequest));
        } catch (IOException e) {
            log.error("LoginRequest parse error", e);
            return Mono.error(new ServerException(LOGIN_BODY_PARSE_ERROR));
        }
    }
}

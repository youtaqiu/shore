package run.vexa.reactor.security.service;

import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.commons.exception.TokenException;
import run.vexa.reactor.security.domain.ClientInfo;
import run.vexa.reactor.security.util.ClientUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 客户端信息服务.
 * @author youta
 **/
public interface ClientInfoService {


    /**
     * 获取客户端id
     *
     * @param exchange 请求
     * @return {@link ClientInfo}
     */
    default Mono<ClientInfo> client(ServerWebExchange exchange) {
        var client = ClientUtil.getClient(exchange);
        return this.loadClientById(client.getClientId())
                .doOnError(throwable -> {
                    throw new ServerException("You must implement the loadClientById method");
                })
                .switchIfEmpty(Mono.error(new TokenException("Invalid ClientId")))
                .handle((clientInfo, sink) -> {
                    if (!clientInfo.getClientSecret().equals(client.getClientSecret())) {
                        sink.error(new TokenException("Invalid ClientSecret"));
                        return;
                    }
                    sink.next(clientInfo);
                });
    }

    /**
     * 根据clientId获取客户端信息
     *
     * @param clientId 客户端id
     * @return ClientInfo 客户端信息
     */
    default Mono<ClientInfo> loadClientById(String clientId) {
        return Mono.justOrEmpty(ClientInfo.base(clientId, ""));
    }

}

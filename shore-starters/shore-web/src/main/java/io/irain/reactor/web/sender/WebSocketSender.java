package io.irain.reactor.web.sender;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

/**
 * WebSocket发送器.
 *
 * @author youta
 **/
public class WebSocketSender {

    private final WebSocketSession session;
    private final FluxSink<WebSocketMessage> sink;

    /**
     * 构造函数.
     *
     * @param session session
     * @param sink    sink
     */
    public WebSocketSender(WebSocketSession session, FluxSink<WebSocketMessage> sink) {
        this.session = session;
        this.sink = sink;
    }

    /**
     * 发送数据.
     *
     * @param data 数据
     */
    public void sendData(String data) {
        sink.next(session.textMessage(data));
    }

}

package me.youm.reactor.webflux.sender;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

/**
 * @author youta
 **/
public class WebSocketSender {

    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;

    public WebSocketSender(WebSocketSession session, FluxSink<WebSocketMessage> sink) {
        this.session = session;
        this.sink = sink;
    }

    public void sendData(String data) {
        sink.next(session.textMessage(data));
    }

}

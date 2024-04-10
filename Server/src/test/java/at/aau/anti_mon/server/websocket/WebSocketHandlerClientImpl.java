package at.aau.anti_mon.server.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.util.concurrent.BlockingQueue;

public class WebSocketHandlerClientImpl implements WebSocketHandler {
    private final BlockingQueue<String> messagesQueue;

    public WebSocketHandlerClientImpl(BlockingQueue<String> receivedMessagesQueue){
        messagesQueue = receivedMessagesQueue;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Logger.info("Connection established with session ID: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        Logger.info("Client received message: " + payload);
        messagesQueue.add(payload);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Logger.error("Transport error in session ID: " + session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Logger.info("Connection closed with session ID: " + session.getId() + "; Close status: " + closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

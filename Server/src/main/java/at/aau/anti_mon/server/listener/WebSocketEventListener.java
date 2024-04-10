package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.service.SessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class WebSocketEventListener {
    private final SessionManagementService sessionManagementService;
    private final Lock lock = new ReentrantLock();

    @Autowired
    public WebSocketEventListener(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }


    public void updateSessionSafely(WebSocketSession session, String data) throws IOException {
        lock.lock();
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(data));
            }
        } finally {
            lock.unlock();
        }
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        WebSocketSession session = event.getSession();
        sessionManagementService.registerSession(session);
        Logger.info("Session connected: " + session.getId());
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        sessionManagementService.removeSessionById(sessionId);
        Logger.info("Session disconnected: " + sessionId);
    }
}
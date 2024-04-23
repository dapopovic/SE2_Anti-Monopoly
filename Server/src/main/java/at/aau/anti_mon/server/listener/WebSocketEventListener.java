package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.websocket.manager.HeartBeatManager;
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

/**
 * Event-Listener for WebSocket events and sessions
 */
@Component
public class WebSocketEventListener {

    private final SessionManagementService sessionManagementService;
    private final Lock lock = new ReentrantLock();
    private final HeartBeatManager heartbeatManager;

    @Autowired
    public WebSocketEventListener(
            SessionManagementService sessionManagementService,
            HeartBeatManager heartbeatManager
    ) {
        this.sessionManagementService = sessionManagementService;
        this.heartbeatManager = heartbeatManager;
    }

    /**
     * TODO: TEST
     * @param session WebSocketSession
     * @param data Nachricht
     * @throws IOException
     */
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

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung verbunden wird.
     * @param event Ereignis
     */
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        WebSocketSession session = event.getSession();
        sessionManagementService.registerSession(session);
        if (sessionManagementService.getNumberOfSessions() == 1) {
            heartbeatManager.start();
            Logger.info("HeartBeatManager started");
        }
        Logger.info("Session connected: " + session.getId());
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        sessionManagementService.removeSessionById(sessionId);
        if (sessionManagementService.getNumberOfSessions() == 0) {
            heartbeatManager.stop();
            Logger.info("HeartBeatManager stopped");
        }
        Logger.info("Session disconnected: " + sessionId);
    }
}
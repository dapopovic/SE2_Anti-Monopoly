package at.aau.anti_mon.server.service;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing WebSocket sessions
 */
@Getter
@Setter
@Service
public class SessionManagementService {

    /**
     * Map of all active sessions
     * Key: session id
     * Value: WebSocketSession
     */
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * Map of all session keys
     * Key: User ID
     * Value: session key
     */
    private final Map<String, String> sessionKeys = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
        Logger.info("Session registered: {}", session.getId());
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        Logger.info("Session removed: {}", session.getId());
    }


    public WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSessionById(String sessionId) {
        try (WebSocketSession session = sessions.get(sessionId)) {
            if (session != null) {
                sessions.remove(sessionId);
            }
        } catch (IOException e) {
            System.err.println("Error closing session " + sessionId);
        }
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return sessions;
    }

    public int getNumberOfSessions() {
        return sessions.size();
    }

}
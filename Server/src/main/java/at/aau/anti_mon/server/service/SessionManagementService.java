package at.aau.anti_mon.server.service;


import at.aau.anti_mon.server.exceptions.SessionNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handling aller Aspekte der WebSocketSessions
 * - Erstellung, Speicherung, Schließung von Sessions
 *
 * TODO --> Nutzen von Optional<> für besseres Handling!
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
    private final Map<String, WebSocketSession> sessions;

    /**
     * Map of all session keys
     * Key: User ID
     * Value: session key
     */
    private final Map<String, String> userSessionMap;


    public SessionManagementService() {
        sessions = new ConcurrentHashMap<>();
        userSessionMap = new ConcurrentHashMap<>();
    }

    /**
     * Registers a user with a session
     * @param userId The user ID
     * @param session The WebSocketSession
     */
    public void registerUserWithSession(String userId, WebSocketSession session) {
        if (userId == null) {
            Logger.warn("UserID is null!", session.getId(), "null");
            throw new IllegalArgumentException("UserID is null!");
        }

        sessions.put(session.getId(), session);
        userSessionMap.put(userId, session.getId());

        Logger.info("Session {} registered with user: {}", session.getId(), userId);
    }

    public WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSessionById(String sessionId, String userId ) {
        sessions.remove(sessionId);
        userSessionMap.remove(userId, sessionId);
        Logger.info("Removed session by ID: {}  For user: {}  ", sessionId,userId);
    }

    public WebSocketSession getSessionForUser(String userId) throws SessionNotFoundException {
        if (!userSessionMap.containsKey(userId)) {
            Logger.error("User {} not found", userId);
            throw new SessionNotFoundException("User not found");
        }
        String sessionId = userSessionMap.get(userId);
        return getSession(sessionId);
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return sessions;
    }

    public int getNumberOfSessions() {
        return sessions.size();
    }

}
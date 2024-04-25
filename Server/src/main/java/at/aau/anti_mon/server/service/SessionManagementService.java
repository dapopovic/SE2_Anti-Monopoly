package at.aau.anti_mon.server.service;


import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.User;
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
        // Für genaue Key-Value-Prüfung
        //if (userSessionMap.get(userId).equals(session.getId())) {
        //       Logger.info("Session {} already registered with user: {}", session.getId(), userId);
        //       return;
        //}

        sessions.put(session.getId(), session);

        if (userId == null) {
            Logger.warn("Session {} registered with user: {}", session.getId(), "null");
            return;
        }
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

    public WebSocketSession getSessionForUser(String userId) {
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
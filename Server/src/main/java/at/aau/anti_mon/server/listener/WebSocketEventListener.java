package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.events.SessionCheckEvent;
import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.events.SessionDisconnectEvent;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.utilities.StringUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.net.URI;
import java.util.Objects;

/**
 * Event-Listener for WebSocket events and sessions
 */
@Component
public class WebSocketEventListener {

    private final SessionManagementService sessionManagementService;

    @Autowired
    public WebSocketEventListener(
            SessionManagementService sessionManagementService
    ) {
        this.sessionManagementService = sessionManagementService;
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung verbunden wird.
     * @param event SessionConnectEvent
     */
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        WebSocketSession session = event.getSession();
        if (session == null || session.getUri() == null) {
            Logger.error("Session or URI is null in handleSessionConnected");
            return;
        }

        String userID = StringUtility.extractUserID(session.getUri().getQuery());
        if (userID.equals("null")) {
            Logger.error("UserID extraction failed for session: {}", session.getId());
        } else {
            sessionManagementService.registerUserWithSession(userID, session);
            Logger.info("Session connected: {} for UserID: {}", session.getId(), userID);
        }
    }

    /**
     * Achtung: nicht SessionDisconnectEvent von Spring Messages
     * @param event SessionDisconnectEvent
     */
    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        WebSocketSession session = event.getSession();
        if (isUriNull(event.getSession())) return;
        String userID = StringUtility.extractUserID(Objects.requireNonNull(session.getUri()).getQuery());
        if (userID.equals("null")) {
            Logger.error("UserID extraction failed for session: {}", session.getId());
        } else {
            sessionManagementService.removeSessionById(session.getId(), userID);
            Logger.info("Session disconnected: {} for UserID: {}", session.getId(), userID);
        }
    }


    /**
     * --> HeartBeatEvent
     * @param event SessionCheckEvent
     */
    @EventListener
    public void handleSessionCheckEvent(SessionCheckEvent event) {
        if (isUriNull(event.getSession())) return;
        WebSocketSession session = event.getSession();
        Logger.info("Heartbeat received from: " + session.getId());
    }

    private boolean isUriNull(WebSocketSession session) {
        URI sessionUri = session.getUri();
        if (sessionUri == null) {
            Logger.error("URI ist null");
            Logger.info("Session connected: " + session.getId());
            return true;
        }

        String userID = StringUtility.extractUserID(sessionUri.getQuery());
        sessionManagementService.registerUserWithSession( userID, session);
        return false;
    }

}
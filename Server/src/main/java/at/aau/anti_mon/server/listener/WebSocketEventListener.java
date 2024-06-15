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
     * Achtung: nicht SessionConnectEvent von Spring Messages
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung verbunden wird.
     * @param event Ereignis
     */
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        if (uriisNull(event.getSession())) return;
        Logger.info("Session connected: " + event.getSession().getId());
    }

    /**
     * Achtung: nicht SessionDisconnectEvent von Spring Messages
     * @param event SessionDisconnectEvent
     */
    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        Logger.info("Session disconnected: " + event.getSession().getId());
    }


    /**
     * --> HeartBeatEvent
     * @param event SessionCheckEvent
     */
    @EventListener
    public void handleSessionCheckEvent(SessionCheckEvent event) {
        if (uriisNull(event.getSession())) return;
        WebSocketSession session = event.getSession();
        Logger.info("Heartbeat received from: " + session.getId());
    }

    private boolean uriisNull(WebSocketSession session) {
        if (session.getUri() == null) {
            Logger.error("URI ist null");
            Logger.info("Session connected: " + session.getId());
            return true;
        }

        String userID = StringUtility.extractUserID(session.getUri().getQuery());
        sessionManagementService.registerUserWithSession( userID, session);
        return false;
    }

}
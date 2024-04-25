package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.events.SessionCheckEvent;
import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.events.SessionDisconnectEvent;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.websocket.manager.HeartBeatManager;
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
     * Achtung: nicht SessionConnectEvent von Spring Messages
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung verbunden wird.
     * @param event Ereignis
     */
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        String userID = extractUserID(event.getSession().getUri().getQuery().toString());
        sessionManagementService.registerUserWithSession( userID,event.getSession());

       sessionManagementService.registerUserWithSession(userID,event.getSession());
       // if (sessionManagementService.getNumberOfSessions() == 1) {
         //   heartbeatManager.start();
        //    Logger.info("HeartBeatManager started");
       // }
        Logger.info("Session connected: " + event.getSession().getId());
    }

    /**
     * Achtung: nicht SessionDisconnectEvent von Spring Messages
     * @param event
     */
    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
      //  sessionManagementService.removeSessionById( event.getSession().getId(), event.getUserID());
       // if (sessionManagementService.getNumberOfSessions() == 0) {
        //    heartbeatManager.stop();
        //    Logger.info("HeartBeatManager stopped");
       // }
        Logger.info("Session disconnected: " + event.getSession().getId());
    }


    /**
     * --> HeartBeatEvent
     * @param event SessionCheckEvent
     */
    @EventListener
    public void handleSessionCheckEvent(SessionCheckEvent event) {

        String userID = extractUserID(event.getSession().getUri().getQuery().toString());
        sessionManagementService.registerUserWithSession( userID,event.getSession());

        WebSocketSession session = event.getSession();
        Logger.info("Heartbeat received from: " + session.getId());

    }

    private String extractUserID(String query) {
        String[] params = query.split("&");
        String userIdKey = "userID=";
        for (String param : params) {
            if (param.startsWith(userIdKey)) {
                return param.substring(userIdKey.length());
            }
        }
        Logger.error("UserID konnte nicht extrahiert werden.");
        return null; // Oder eine angemessene Fehlerbehandlung, falls die userID nicht gefunden wird
    }


}
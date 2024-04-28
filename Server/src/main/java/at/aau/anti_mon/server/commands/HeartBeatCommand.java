package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.events.SessionCheckEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.game.JsonDataDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;


/**
 * Command to handle the heartbeat of the client
 */
public class HeartBeatCommand implements Command{

    private final ApplicationEventPublisher eventPublisher;

    public HeartBeatCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        // data = {"msg": PING }
        if (jsonData.getData() == null || jsonData.getData().get("msg") == null) {
            Logger.error("SERVER: Required data for 'HEARTBEAT' is missing.");
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'HEARTBEAT' is missing.");
        }

        Logger.info("SERVER : Heartbeat empfangen."+jsonData.getData().get("msg"));

        String userID = extractUserID(session.getUri().getQuery());
        Logger.info( "Query " + session.getUri().getQuery() );

        eventPublisher.publishEvent(new SessionCheckEvent(session,userID));
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
        throw new IllegalArgumentException("UserID konnte nicht extrahiert werden.");
    }
}

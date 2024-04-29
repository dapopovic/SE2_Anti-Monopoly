package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.events.SessionCheckEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.utilities.StringUtility;
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

        // Todo Test
        if (session.getUri() == null) {
            Logger.error("SERVER: Required data for 'HEARTBEAT' is missing.");
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'HEARTBEAT' is missing.");
        }else{
            String userID = StringUtility.extractUserID(session.getUri().getQuery());
            Logger.info( "Query " + session.getUri().getQuery() );
            eventPublisher.publishEvent(new SessionCheckEvent(session,userID));
        }

    }


}

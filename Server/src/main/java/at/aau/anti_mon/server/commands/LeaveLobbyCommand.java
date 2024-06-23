package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Command to leave a lobby

 */
public class LeaveLobbyCommand implements Command{

    private final ApplicationEventPublisher eventPublisher;

    public LeaveLobbyCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        // data = {"username": "Test" , "pin": "1234"

        if (jsonData.getData() != null){
            Logger.info("SERVER: Data for 'LEAVE_GAME' is not null. " + jsonData.getData().toString());
        }

        if (jsonData.getData() == null || jsonData.getData().get("pin") == null || jsonData.getData().get("username") == null) {
            Logger.error("SERVER: Required data for 'LEAVE_GAME' is missing.");
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'LEAVE_GAME' is missing.");
        }

        String playerName = jsonData.getData().get("username");
        String pinString = jsonData.getData().get("pin");

        Logger.info("SERVER : LEAVE_GAME empfangen." + playerName + " hat die Lobby mit der PIN " + pinString + " verlassen.");

        eventPublisher.publishEvent(new UserLeftLobbyEvent(session, Integer.parseInt(pinString), playerName));

    }
}

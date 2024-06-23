package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Command to join a lobby
 */
public class JoinLobbyCommand implements Command{

    private final ApplicationEventPublisher eventPublisher;

    public JoinLobbyCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        if (jsonData.getData() == null || jsonData.getData().get("pin") == null || jsonData.getData().get("username") == null) {
            Logger.error("SERVER: Required data for 'JOIN' is missing.");
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'JOIN' is missing.");
        }

        String pinString = jsonData.getData().get("pin");
        String playerName = jsonData.getData().get("username");

        Logger.info("SERVER: JSON Data : User {} joined lobby with pin {}", playerName, pinString);

        int pin = Integer.parseInt(pinString);
        eventPublisher.publishEvent(new UserJoinedLobbyEvent(session,pin, playerName));
    }

}

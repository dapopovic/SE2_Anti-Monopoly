package at.aau.anti_mon.server.commands;


import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserCreatedLobbyEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.game.JsonDataDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Command to create a new game session
 */
public class CreateGameCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    public CreateGameCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        // data = {"username": "Test"}
        if (jsonData.getData() == null || jsonData.getData().get("username") == null) {
            Logger.error("SERVER: Required data for 'CREATE_GAME' is missing.");
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'CREATE_GAME' is missing.");
        }


        String playerName = jsonData.getData().get("username");
        UserDTO playerDTO = new UserDTO(playerName, true, true);

        Logger.info("SERVER: JSON Data : User {} creates Lobby", playerName);
        eventPublisher.publishEvent(new UserCreatedLobbyEvent(session, playerDTO));
    }
}

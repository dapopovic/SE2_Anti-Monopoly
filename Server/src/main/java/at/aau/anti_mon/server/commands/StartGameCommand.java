package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserReadyLobbyEvent;
import at.aau.anti_mon.server.events.UserStartedGameEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;

public class StartGameCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    public StartGameCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        if (jsonData.getData() == null || jsonData.getData().get("pin") == null || jsonData.getData().get("username") == null) {
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'START_GAME' is missing.");
        }

        String playerName = jsonData.getData().get("username");
        String pinString = jsonData.getData().get("pin");

        eventPublisher.publishEvent(new UserStartedGameEvent(session, new LobbyDTO(Integer.parseInt(pinString)), new UserDTO(playerName, true, true, null, null)));
    }
}

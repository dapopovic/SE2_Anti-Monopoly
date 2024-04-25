package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.GameSessionDTO;
import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.game.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
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
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws Exception {
        // data = {"pin": 1234 , "name": "Test"}
        String pinString = jsonData.getData().get("pin");
        String playerName = jsonData.getData().get("username");

        if (pinString != null && playerName != null) {
            Logger.info("SERVER: JSON Data : User {} joined lobby with pin {}", playerName, pinString);

            int pin = Integer.parseInt(pinString);
            UserDTO playerDTO = new UserDTO(playerName);
            LobbyDTO lobbyDTO = new LobbyDTO(pin);
            //GameSessionDTO gameSessionDTO = new GameSessionDTO(session.getId(),playerDTO, lobbyDTO);
            eventPublisher.publishEvent(new UserJoinedLobbyEvent(session, lobbyDTO, playerDTO));
        } else {
            Logger.error("SERVER: Required data for 'JOIN_GAME' is missing.");
        }
    }

}

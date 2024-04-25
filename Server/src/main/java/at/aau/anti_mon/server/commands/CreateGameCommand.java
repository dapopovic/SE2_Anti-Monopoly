package at.aau.anti_mon.server.commands;


import at.aau.anti_mon.server.dtos.GameSessionDTO;
import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.CreateLobbyEvent;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.game.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
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
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws Exception {
        // data = {"username": "Test"}
        String playerName = jsonData.getData().get("username");
        UserDTO playerDTO = new UserDTO(playerName);

        if (playerName != null) {
            Logger.info("SERVER: JSON Data : User {} creates Lobby", playerName);
            //GameSessionDTO gameSessionDTO = new GameSessionDTO(session.getId(),playerDTO);
            eventPublisher.publishEvent(new CreateLobbyEvent(session, playerDTO));
        } else {
            Logger.error("SERVER: JSON 'data' ist null oder 'username' ist nicht vorhanden.");
        }
    }
}

package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.GameSessionDTO;
import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.game.JsonDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
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
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws Exception {
        // data = {"username": "Test" , "pin": "1234"
        String playerName = jsonData.getData().get("username");
        String pinString = jsonData.getData().get("pin");

        if (pinString != null && playerName != null) {

            UserDTO playerDTO = new UserDTO(playerName);
            LobbyDTO lobbyDTO = new LobbyDTO(Integer.parseInt(pinString));
            //GameSessionDTO gameSessionDTO = new GameSessionDTO(session.getId(), playerDTO, lobbyDTO);
            eventPublisher.publishEvent(new UserLeftLobbyEvent(session, lobbyDTO, playerDTO));

        } else {
            Logger.error("SERVER : 'LEAVE_GAME' enthält kein 'name'-Attribut.");
        }
        Logger.info("SERVER : LEAVE_GAME empfangen." + jsonData.getData().get("username"));
    }
}

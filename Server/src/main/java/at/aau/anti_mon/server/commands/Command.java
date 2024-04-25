package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.game.JsonDataDTO;
import org.springframework.web.socket.WebSocketSession;

public interface Command {
    void execute(WebSocketSession session, JsonDataDTO jsonData) throws Exception;
}

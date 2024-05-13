package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import org.springframework.web.socket.WebSocketSession;

public interface Command {
    void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException;
}

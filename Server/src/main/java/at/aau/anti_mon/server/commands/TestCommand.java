package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Command to test the connection
 */
public class TestCommand implements Command {
    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        Logger.debug("TestCommand executed");
    }
}

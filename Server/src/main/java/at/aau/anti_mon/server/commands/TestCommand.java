package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.game.JsonDataDTO;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Command to test the connection
 */
public class TestCommand implements Command {
    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws Exception {
        Logger.debug("TestCommand executed");
    }
}

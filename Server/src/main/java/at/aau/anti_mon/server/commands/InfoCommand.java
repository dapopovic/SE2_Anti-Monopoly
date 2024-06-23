package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

public class InfoCommand implements Command {

    public InfoCommand() {
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        String infoMessage = jsonData.getData().get("msg");
        String infoContext = jsonData.getData().get("context");

        Logger.info("InfoCommand", "Info received: " + infoMessage + " for context: " + infoContext);

    }

}

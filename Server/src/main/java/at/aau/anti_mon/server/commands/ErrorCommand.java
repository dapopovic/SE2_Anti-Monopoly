package at.aau.anti_mon.server.commands;


import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

public class ErrorCommand implements Command{

    public ErrorCommand() {
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        String errorMessage = jsonData.getData().get("msg");
        String errorContext = jsonData.getData().get("context");

        Logger.error("ErrorCommand", "Error received: " + errorMessage + " for context: " + errorContext);

    }
}

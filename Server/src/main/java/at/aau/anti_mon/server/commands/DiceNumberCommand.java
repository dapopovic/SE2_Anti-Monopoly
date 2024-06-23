package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.DiceNumberEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Command to set the dice number
 */
public class DiceNumberCommand implements Command{

    private final ApplicationEventPublisher eventPublisher;
    public DiceNumberCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        if (jsonData.getData() == null
                || jsonData.getData().get("username") == null
                || jsonData.getData().get("dicenumber") == null
                || jsonData.getData().get("pin") == null
                || jsonData.getData().get("cheat") == null){
            Logger.error("SERVER: Required data for 'DICE_NUMBER' is missing.");
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'DICE_NUMBER' is missing.");
        }

        String username = jsonData.getData().get("username");
        Integer dicenumber = Integer.valueOf(jsonData.getData().get("dicenumber"));
        Integer pin = Integer.valueOf(jsonData.getData().get("pin"));
        Boolean cheat = Boolean.valueOf(jsonData.getData().get("cheat"));

        eventPublisher.publishEvent(new DiceNumberEvent(session,username, dicenumber, pin, cheat));
    }
}

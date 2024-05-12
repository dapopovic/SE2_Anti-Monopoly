package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.events.DicesNumberReceivedEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.game.JsonDataDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

public class DiceNumbersCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    public DiceNumbersCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        if (jsonData.getData() == null || jsonData.getData().get("number") == null) {
            Logger.error("SERVER: Required data is missing.");
            throw new CanNotExecuteJsonCommandException("SERVER: Required data for 'DICES_NUMBER' is missing.");
        }

        String number = jsonData.getData().get("number");

        // maybe some actions

        eventPublisher.publishEvent(new DicesNumberReceivedEvent(session, number));

    }
}

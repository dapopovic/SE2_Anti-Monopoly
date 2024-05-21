package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.ChangeBalanceEvent;
import at.aau.anti_mon.server.events.DiceNumberEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;

public class ChangeBalanceCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    public ChangeBalanceCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        String username = jsonData.getData().get("username");
        Integer newBalance = Integer.valueOf(jsonData.getData().get("new_balance"));

        eventPublisher.publishEvent(new ChangeBalanceEvent(session, username, newBalance));
    }
}

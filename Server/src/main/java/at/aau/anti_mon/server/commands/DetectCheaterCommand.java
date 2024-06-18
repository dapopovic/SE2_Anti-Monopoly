package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.ChangeBalanceEvent;
import at.aau.anti_mon.server.events.DetectCheaterEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;

public class DetectCheaterCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    public DetectCheaterCommand(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        String username = jsonData.getData().get("username");
        String cheating_username = jsonData.getData().get("cheating_user");

        eventPublisher.publishEvent(new DetectCheaterEvent(session, username, cheating_username));
    }
}

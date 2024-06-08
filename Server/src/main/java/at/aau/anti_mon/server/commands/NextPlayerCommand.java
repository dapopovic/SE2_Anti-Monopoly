package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.DiceNumberEvent;
import at.aau.anti_mon.server.events.NextPlayerEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

public class NextPlayerCommand implements Command{
    private final ApplicationEventPublisher eventPublisher;
    public NextPlayerCommand(ApplicationEventPublisher eventPublisher) { this.eventPublisher = eventPublisher;}
    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        Logger.info("Wir sind in NextPlayerCommand.");
        String username = jsonData.getData().get("username");
        eventPublisher.publishEvent(new NextPlayerEvent(session,username));
    }
}

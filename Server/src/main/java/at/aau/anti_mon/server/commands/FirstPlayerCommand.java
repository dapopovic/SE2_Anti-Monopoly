package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.FirstPlayerEvent;
import at.aau.anti_mon.server.events.NextPlayerEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

public class FirstPlayerCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;
    public FirstPlayerCommand(ApplicationEventPublisher eventPublisher) { this.eventPublisher = eventPublisher;}
    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        Logger.info("Wir sind in FirstPlayerCommand.");
        String username = jsonData.getData().get("username");
        eventPublisher.publishEvent(new FirstPlayerEvent(session,username));
    }
}

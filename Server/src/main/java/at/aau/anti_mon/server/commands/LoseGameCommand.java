package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.LooseGameEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

public class LoseGameCommand implements Command{
    private final ApplicationEventPublisher eventPublisher;
    public LoseGameCommand(ApplicationEventPublisher eventPublisher) { this.eventPublisher = eventPublisher;}
    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {
        Logger.info("Wir sind in LoseGameCommand.");
        String username = jsonData.getData().get("username");
        eventPublisher.publishEvent(new LooseGameEvent(session,username));
    }
}
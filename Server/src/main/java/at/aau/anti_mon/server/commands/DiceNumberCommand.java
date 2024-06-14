package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.DiceNumberEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;

public class DiceNumberCommand implements Command{

    private final ApplicationEventPublisher eventPublisher;
    public DiceNumberCommand(ApplicationEventPublisher eventPublisher) { this.eventPublisher = eventPublisher;}

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {


        String username = jsonData.getData().get("username");
        Integer dicenumber = Integer.valueOf(jsonData.getData().get("dicenumber"));
        Boolean cheat = Boolean.valueOf(jsonData.getData().get("cheat"));
        eventPublisher.publishEvent(new DiceNumberEvent(session,username, dicenumber, cheat));
    }
}

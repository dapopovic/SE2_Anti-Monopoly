package at.aau.anti_mon.server.commands;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;

public class BuyPropertyCommand  implements Command{

    private ApplicationEventPublisher eventPublisher;

    public BuyPropertyCommand(ApplicationEventPublisher eventPublisher) {
    }

    @Override
    public void execute(WebSocketSession session, JsonDataDTO jsonData) throws CanNotExecuteJsonCommandException {

    }
}

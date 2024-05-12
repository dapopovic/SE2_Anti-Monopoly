package at.aau.anti_mon.server.events;

import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class DicesNumberReceivedEvent extends Event {
    private int number;

    public DicesNumberReceivedEvent(WebSocketSession session, String number) {
        super(session);
        this.number = Integer.parseInt(number);
    }

    public int getNumber() {
        return number;
    }
}

package at.aau.anti_mon.server.events;


import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

public class DiceNumberEvent extends Event {
    @Getter
    private final String username;
    @Getter
    private final String dicenumber;

    public DiceNumberEvent(WebSocketSession session,String username, String dicenumber){
        super(session);
        this.username = username;
        this.dicenumber = dicenumber;
    }
}

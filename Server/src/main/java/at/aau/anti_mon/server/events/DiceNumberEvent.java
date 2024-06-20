package at.aau.anti_mon.server.events;


import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class DiceNumberEvent extends Event {
    private final String username;
    private final Integer dicenumber;
    private final Integer pin;

    public DiceNumberEvent(WebSocketSession session,String username, Integer dicenumber, Integer pin){
        super(session);
        this.username = username;
        this.dicenumber = dicenumber;
        this.pin = pin;
    }
}

package at.aau.anti_mon.server.events;


import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

public class DiceNumberEvent extends Event {
    @Getter
    private final String username;
    @Getter
    private final Integer dicenumber;
    @Getter
    private final Boolean cheat;

    public DiceNumberEvent(WebSocketSession session,String username, Integer dicenumber, Boolean cheat){
        super(session);
        this.username = username;
        this.dicenumber = dicenumber;
        this.cheat = cheat;
    }
}

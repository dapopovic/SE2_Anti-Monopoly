package at.aau.anti_mon.server.events;


import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player rolls the dice
 */
@Getter
public class DiceNumberEvent extends BaseUserEvent {

    private final Integer dicenumber;
    private final Integer pin;
    private final Boolean cheat;

    public DiceNumberEvent(WebSocketSession session,String userName, Integer diceNumber,Integer lobbyPIN, Boolean cheat){
        super(session,userName);
        this.dicenumber = diceNumber;
        this.pin = lobbyPIN;
        this.cheat = cheat;
    }
}

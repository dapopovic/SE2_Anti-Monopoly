package at.aau.anti_mon.client.events;
import lombok.Getter;

@Getter
public class DiceNumberReceivedEvent {
    private final Integer dicenumber;
    private final String name;

    public DiceNumberReceivedEvent(Integer dicenumber,String name){
        this.dicenumber=dicenumber;
        this.name = name;
    }
}

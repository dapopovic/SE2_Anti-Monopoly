package at.aau.anti_mon.client.events;
import lombok.Getter;

@Getter
public class DiceNumberReceivedEvent {
    private final Integer dicenumber;
    private final String username;
    private final Integer location;
    private final String figure;

    public DiceNumberReceivedEvent(Integer dicenumber,String name,String figure, Integer location){
        this.dicenumber=dicenumber;
        this.username = name;
        this.figure = figure;
        this.location = location;
    }
}

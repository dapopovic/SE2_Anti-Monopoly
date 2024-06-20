package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class EndGameEvent {
    private final int rank;
    public EndGameEvent(int rank){
        this.rank = rank;
    }
}

package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class NextPlayerEvent {
    private final String username;
    public NextPlayerEvent(String username){
        this.username = username;
    }
}

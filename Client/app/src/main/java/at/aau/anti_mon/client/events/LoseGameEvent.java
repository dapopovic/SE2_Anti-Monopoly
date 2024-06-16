package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class LoseGameEvent {
    private final String username;
    public LoseGameEvent(String username){
        this.username = username;
    }
}

package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class LooseGameEvent {
    private final String username;
    public LooseGameEvent(String username){
        this.username = username;
    }
}

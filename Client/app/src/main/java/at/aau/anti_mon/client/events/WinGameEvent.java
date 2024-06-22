package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class WinGameEvent {
    private final String username;
    public WinGameEvent(String username){
        this.username = username;
    }
}
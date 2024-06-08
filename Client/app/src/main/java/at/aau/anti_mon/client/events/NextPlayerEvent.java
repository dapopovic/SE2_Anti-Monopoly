package at.aau.anti_mon.client.events;

import at.aau.anti_mon.client.command.NextPlayerCommand;
import lombok.Getter;

@Getter
public class NextPlayerEvent {
    private final String username;
    public NextPlayerEvent(String username){
        this.username = username;
    }
}

package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class CreatedGameEvent {
    private final String pin;

    public CreatedGameEvent(String pin) {
        this.pin = pin;
    }

}
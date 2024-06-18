package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class CanDetectCheaterEvent {
    private final String username;
    private final Boolean canDetectCheater;
    private final Integer location;

    public CanDetectCheaterEvent(String username, boolean canDetectCheater, Integer location) {
        this.username = username;
        this.canDetectCheater = canDetectCheater;
        this.location = location;
    }
}

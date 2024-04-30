package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class PinReceivedEvent {

    private final String pin;

    public PinReceivedEvent(String pin) {
        this.pin = pin;
    }

}

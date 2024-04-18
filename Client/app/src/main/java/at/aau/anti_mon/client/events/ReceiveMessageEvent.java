package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class ReceiveMessageEvent {
    private final String message;

    public ReceiveMessageEvent(String message) {
        this.message = message;
    }

}

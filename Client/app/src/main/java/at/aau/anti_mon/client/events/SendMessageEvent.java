package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class SendMessageEvent {
    private final String message;

    public SendMessageEvent(String message) {
        this.message = message;
    }

}
package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class TestEvent {
    private final String message;

    public TestEvent(String message) {
        this.message = message;
    }

}
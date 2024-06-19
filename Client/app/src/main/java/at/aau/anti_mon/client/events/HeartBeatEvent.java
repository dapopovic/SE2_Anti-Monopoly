package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class HeartBeatEvent {
    private final String heartbeat;

    public HeartBeatEvent(String msg) {
        this.heartbeat = msg;
    }

}

package at.aau.anti_mon.client.events;

public class HeartBeatEvent {
    private final String msg;

    public HeartBeatEvent(String msg) {
        this.msg = msg;
    }

}

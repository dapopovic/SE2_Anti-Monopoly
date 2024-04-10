package at.aau.anti_mon.server.messaging.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutputMessage {

    private final String from;
    private final String text;
    private final String time;

    public OutputMessage(final String from, final String text, final String time) {

        this.from = from;
        this.text = text;
        this.time = time;
    }

}

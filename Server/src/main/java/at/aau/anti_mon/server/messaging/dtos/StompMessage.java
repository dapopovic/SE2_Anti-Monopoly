package at.aau.anti_mon.server.messaging.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StompMessage {

        private String from;
        private String text;

}

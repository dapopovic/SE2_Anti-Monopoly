package at.aau.anti_mon.server.messaging.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private int userID;
    private int fromUserID;
    private String message;
}
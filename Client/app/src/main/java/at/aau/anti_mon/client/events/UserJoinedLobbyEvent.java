package at.aau.anti_mon.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinedLobbyEvent {
    private final String name;
    private final boolean isOwner;
    private final boolean isReady;

}

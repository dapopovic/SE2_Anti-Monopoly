package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class UserJoinedLobbyEvent {
    private final String name;

    public UserJoinedLobbyEvent(String name) {
        this.name = name;
    }
}

package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class UserLeftLobbyEvent {
    private final String name;

    public UserLeftLobbyEvent(String name) {
        this.name = name;
    }
}
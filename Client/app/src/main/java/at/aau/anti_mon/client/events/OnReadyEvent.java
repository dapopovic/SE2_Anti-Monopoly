package at.aau.anti_mon.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnReadyEvent {
    private final String userName;
}

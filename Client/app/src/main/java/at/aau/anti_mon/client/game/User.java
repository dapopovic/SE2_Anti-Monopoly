package at.aau.anti_mon.client.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class User {
    private String username;
    private boolean isOwner;
    @Setter
    private boolean isReady;
}

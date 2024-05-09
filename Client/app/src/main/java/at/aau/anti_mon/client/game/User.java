package at.aau.anti_mon.client.game;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    private String username;
    @JsonProperty("owner")
    private boolean isOwner;
    @Setter
    private boolean isReady;
}

package at.aau.anti_mon.server.game;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Command {
    private final Commands command;
    private final JsonObject data;
}

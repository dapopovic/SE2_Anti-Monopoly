package at.aau.anti_mon.server.game;

import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class Command {
    private final Commands command;
    private final JsonObject data;

    public Command(Commands command, JsonObject data) {
        this.command = command;
        this.data = data;
    }
}

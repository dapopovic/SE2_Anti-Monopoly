package at.aau.anti_mon.server.game;

import lombok.Getter;

@Getter
public class Command {
    private final Commands command;
    private final String data;

    public Command(Commands command, String data) {
        this.command = command;
        this.data = data;
    }
}

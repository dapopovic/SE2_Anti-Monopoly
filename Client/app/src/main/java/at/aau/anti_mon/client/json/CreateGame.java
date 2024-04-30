package at.aau.anti_mon.client.json;

import at.aau.anti_mon.client.command.Commands;

public class CreateGame {
    private Commands command;
    private Data data;

    public CreateGame(Commands command,Data data){
        this.command=command;
        this.data = data;
    }
}

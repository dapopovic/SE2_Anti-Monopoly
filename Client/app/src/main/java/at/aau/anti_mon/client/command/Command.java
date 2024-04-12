package at.aau.anti_mon.client.command;

import at.aau.anti_mon.client.json.JsonDataDTO;

public interface Command {
    void execute(JsonDataDTO data);
}

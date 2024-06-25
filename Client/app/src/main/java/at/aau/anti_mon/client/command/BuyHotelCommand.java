package at.aau.anti_mon.client.command;

import at.aau.anti_mon.client.json.JsonDataDTO;
import lombok.Getter;

@Getter
public class BuyHotelCommand implements Command {

    private final int fieldId;

    public BuyHotelCommand(int fieldId) {
        this.fieldId = fieldId;
    }


    @Override
    public void execute(JsonDataDTO data) {

    }
}

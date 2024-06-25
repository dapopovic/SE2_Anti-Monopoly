package at.aau.anti_mon.client.command;

import at.aau.anti_mon.client.json.JsonDataDTO;
import lombok.Getter;

@Getter
public class BuyHouseCommand implements Command{

    private final int fieldId;

    public BuyHouseCommand(int fieldId) {
        this.fieldId = fieldId;
    }


    @Override
    public void execute(JsonDataDTO data) {

    }

}

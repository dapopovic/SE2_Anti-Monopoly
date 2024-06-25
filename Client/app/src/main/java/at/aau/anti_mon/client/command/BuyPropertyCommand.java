package at.aau.anti_mon.client.command;

import at.aau.anti_mon.client.json.JsonDataDTO;
import lombok.Getter;

@Getter
public class BuyPropertyCommand implements Command{

    private final int fieldId;

    public BuyPropertyCommand(int fieldId) {
        this.fieldId = fieldId;
    }


    @Override
    public void execute(JsonDataDTO data) {

    }

}

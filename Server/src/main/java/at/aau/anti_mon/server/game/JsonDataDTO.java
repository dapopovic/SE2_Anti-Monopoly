package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.Commands;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class JsonDataDTO {

    private Commands command;
    private Map<String, String> data;

    public JsonDataDTO(Commands command, Map<String, String> data) {
        this.command = command;
        this.data = data;
    }

    public JsonDataDTO() {
    }

    @JsonAnyGetter
    public Map<String, String> getData() {
        return data;
    }

    @JsonAnySetter
    public void setData(String key, String value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }


}

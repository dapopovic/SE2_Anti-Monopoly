package at.aau.anti_mon.client.json;



import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

import at.aau.anti_mon.client.enums.Commands;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object that is used to send data between the server and the clients
 */
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
    public void putData(String key, String value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }


}

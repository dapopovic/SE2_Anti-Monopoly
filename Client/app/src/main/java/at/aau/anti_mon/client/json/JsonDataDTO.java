package at.aau.anti_mon.client.json;



import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import at.aau.anti_mon.client.enums.Commands;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object that is used to send data between the server and the clients
 */
@Getter
@Setter
public class JsonDataDTO implements Serializable {

    private Commands command;
    private Map<String, String> data;

    public JsonDataDTO(Commands command) {
        this.command = command;
        this.data = new HashMap<>();
    }

    public JsonDataDTO(Commands command, Map<String, String> data) {
        this.command = command;
        this.data = data;
    }

    public JsonDataDTO() {
        this.data = new HashMap<>();
    }

    @JsonAnyGetter
    public Map<String, String> getData() {
        return data;
    }

    @JsonAnySetter
    public void putData(String key, String value) {
        data.put(key, value);
    }


    public static class Builder {
        private final Commands command;
        private final Map<String, String> data = new HashMap<>();

        public Builder(Commands command) {
            this.command = command;
        }

        public Builder addObject(String key, Object value) {
            data.put(key, value.toString());
            return this;
        }

        public Builder addString(String key, String value) {
            data.put(key, value);
            return this;
        }

        public Builder addInt(String key, Integer value) {
            data.put(key, value.toString());
            return this;
        }

        public Builder addBoolean(String key, Boolean value) {
            data.put(key, value.toString());
            return this;
        }

        public JsonDataDTO build() {
            return new JsonDataDTO(command, data);
        }
    }
}

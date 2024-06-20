package at.aau.anti_mon.server.dtos;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;
import org.tinylog.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        public Builder addUser(String key, UserDTO user) {
            data.put(key, user.toString());
            return this;
        }

        public Builder addUserCollection(String key, Collection<UserDTO> userDTOS) {
            StringBuilder usersString = new StringBuilder();
            for (UserDTO user : userDTOS) {
                usersString.append(JsonDataUtility.createStringFromJsonMessage(user)).append(",");
            }
            usersString.deleteCharAt(usersString.length() - 1);
            data.put(key, "[" + usersString + "]");
            return this;
        }

        public JsonDataDTO build() {
            Logger.info("Building JsonDataDTO with command: " + command + " and data: " + data);
            return new JsonDataDTO(command, data);
        }
    }


}

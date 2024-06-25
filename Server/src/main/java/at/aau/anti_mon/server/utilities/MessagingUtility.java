package at.aau.anti_mon.server.utilities;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.game.User;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Collection;

/**
 * Mediator class to reduce the complexity between JsonDataManager and WebSocketClient.
 * This class is responsible for creating and sending messages
 */
@Component
public class MessagingUtility {

    public static MessageContainer createHeartbeatMessage() {
        return new MessageContainer(new JsonDataDTO.Builder(Commands.HEARTBEAT).addString("msg","PING").build());
    }

    /**
     * Creates a JSON message for the game with the dice number
     * { username: String, dicenumber: int, command: Commands }
     * @param username the username of the game
     * @param dice the dice number
     * @return the JSON message as a string
     */
    public static MessageContainer createGameMessage(String username, Integer dice, Commands command) {

        Logger.info("Creating game message with username: " + username + " and dice: " + dice);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addInt("dicenumber", dice)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game with the dice number
     * { username: String, dicenumber: int, command: Commands }
     * @param username the username of the game
     * @param dice the dice number
     * @return the JSON message as a string
     */
    public static MessageContainer createGameMessage(String username, Integer dice, Figures figure, Integer location, Commands command) {

        Logger.info("Creating game message with username: " + username + " and dice: " + dice + " and figure: " + figure + " and location: " + location);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addInt("dicenumber", dice)
                .addString("figure", figure.toString())
                .addInt("location", location)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { username: String, pin: String, command: Commands }
     * @param username the username of the game
     * @param pin the pin of the game -> method is overloaded
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createUserMessage(String username, String pin, Commands command) {

        Logger.info("Creating user message with username: " + username + " and pin: " + pin);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addString("pin", pin)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    public static MessageContainer createUserMessage(User user, Commands command) {

        Logger.info("Creating user message with object: " + user.toString());

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", user.getUserName())
                .addBoolean("isReady", user.isReady())
                .addBoolean("isOwner", user.isOwner())
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    public static MessageContainer createUsernameMessage(String username, Commands command) {

        Logger.info("Creating username message with username: " + username);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    public static MessageContainer createJoinedUserMessage(User user) {

        Logger.info("Creating joined user message with object: " + user.toString());

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.NEW_USER)
                .addString("username", user.getUserName())
                .addBoolean("isOwner", user.isOwner())
                .addBoolean("isReady", user.isReady())
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    public static MessageContainer createUserCollectionMessage(Commands command, Collection<UserDTO> userDTOS) {

        Logger.info("Creating user collection message with object: " + userDTOS.toString());

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.START_GAME)
                .addUserCollection("users", userDTOS)
                .build();

        return new MessageContainer(jsonDataDTO);
    }



    /**
     * Creates a JSON message for the game
     * { msg: String, command: Commands }
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createMessage(String key, String value, Commands command) {

        Logger.info("Creating message with key: " + key + " and value: " + value);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString(key, value)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { objectname: Object, command: Commands }
     * @param objectName the name of the object
     * @param object the object to be sent
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createMessage(String objectName, Object object, Commands command) {

        Logger.info("Creating message with objectname: " + objectName + " and object: " + object.toString() + " and command: " + command.toString());

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addObject(objectName, object)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message with error
     * { command: Commands , msg: String , context: String }
     * @param msg the message to be sent
       @param context the context of the message
     * @return the JSON message as a string
     */
    public static MessageContainer createErrorMessage(String msg, String context) {

        Logger.info("Creating error message with msg: " + msg + " and context: " + context);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.ERROR)
                .addString("msg", msg)
                .addString("context", context)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message with information
     * { command: Commands , msg: String , context: String }
     * @param msg the message to be sent
     @param context the context of the message
      * @return the JSON message as a string
     */
    public static MessageContainer createInfoMessage(String msg, String context) {

        Logger.info("Creating info message with msg: " + msg + " and context: " + context);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.INFO)
                .addString("msg", msg)
                .addString("context", context)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    @Getter
    public static class MessageContainer {
        private final String message;

        private MessageContainer(JsonDataDTO jsonDataDTO) {
            this.message = JsonDataUtility.createStringFromJsonMessage(jsonDataDTO);
            if (this.message == null) {
                throw new IllegalArgumentException("Message must not be null");
            }
        }

        public void send(WebSocketSession session) {
            if (session == null) {
                Logger.error("SESSION is null");
                return;
            }
            if (session.isOpen()) {
                try {
                    Logger.info("SERVER: Nachricht senden: " + message);
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    Logger.error("Fehler beim Senden der Nachricht: " + e.getMessage());
                }
            } else {
                Logger.error("SERVER: Versuch, eine Nachricht zu senden, aber die Session ist bereits geschlossen.");
            }
        }
    }
}

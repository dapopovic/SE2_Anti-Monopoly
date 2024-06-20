package at.aau.anti_mon.server.utilities;

import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.game.User;
import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Collection;

/**
 * Facade class to reduce the complexity between JsonDataManager and WebSocketClient.
 * This class is responsible for sending and receiving messages.
 * Test
 */
public class MessagingUtility {




    public static MessageSender createHeartbeatMessage() {
        return new MessageSender(new JsonDataDTO.Builder(Commands.HEARTBEAT).addString("msg","PING").build());
    }

    /**
     * Creates a JSON message for the game with the dice number
     * { username: String, dicenumber: int, command: Commands }
     * @param username the username of the game
     * @param dice the dice number
     * @return the JSON message as a string
     */
    public static MessageSender createGameMessage(String username, Integer dice, Commands command) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addInt("dicenumber", dice)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game with the dice number
     * { username: String, dicenumber: int, command: Commands }
     * @param username the username of the game
     * @param dice the dice number
     * @return the JSON message as a string
     */
    public static MessageSender createGameMessage(String username, Integer dice, Figures figure, Integer location, Commands command) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addInt("dicenumber", dice)
                .addString("figure", figure.toString())
                .addInt("location", location)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { username: String, pin: String, command: Commands }
     * @param username the username of the game
     * @param pin the pin of the game -> method is overloaded
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createUserMessage(String username, String pin, Commands command) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addString("pin", pin)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    public static MessageSender createUserMessage(User user, Commands command) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", user.getName())
                .addBoolean("isReady", user.isReady())
                .addBoolean("isOwner", user.isOwner())
                .build();

        return new MessageSender(jsonDataDTO);
    }

    public static MessageSender createUsernameMessage(String username, Commands command) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    public static MessageSender createJoinedUserMessage(User user) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.NEW_USER)
                .addString("username", user.getName())
                .addBoolean("isOwner", user.isOwner())
                .addBoolean("isReady", user.isReady())
                .build();

        return new MessageSender(jsonDataDTO);
    }

    public static MessageSender createUserCollectionMessage(Commands command, Collection<UserDTO> userDTOS) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.START_GAME)
                .addUserCollection("users", userDTOS)
                .build();


        return new MessageSender(jsonDataDTO);
    }



    /**
     * Creates a JSON message for the game
     * { msg: String, command: Commands }
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createMessage(String key, String value, Commands command) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString(key, value)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { objectname: Object, command: Commands }
     * @param objectname the name of the object
     * @param object the object to be sent
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createMessage(String objectname, Object object, Commands command) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addObject(objectname, object)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message with error
     * { command: Commands , msg: String , context: String }
     * @param msg the message to be sent
       @param context the context of the message
     * @return the JSON message as a string
     */
    public static MessageSender createErrorMessage(String msg, String context) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.ERROR)
                .addString("msg", msg)
                .addString("context", context)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message with information
     * { command: Commands , msg: String , context: String }
     * @param msg the message to be sent
     @param context the context of the message
      * @return the JSON message as a string
     */
    public static MessageSender createInfoMessage(String msg, String context) {

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(Commands.INFO)
                .addString("msg", msg)
                .addString("context", context)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    @Getter
    public static class MessageSender {
        private final String message;

        private MessageSender(JsonDataDTO jsonDataDTO) {
            this.message = JsonDataUtility.createStringFromJsonMessage(jsonDataDTO);
        }

        public boolean send(WebSocketSession session) {
            if (session == null) {
                Logger.error("SESSION is null");
                return false;
            }
            if (session.isOpen()) {
                try {
                    Logger.info("SERVER: Nachricht senden: " + message);
                    session.sendMessage(new TextMessage(message));
                    return true;
                } catch (IOException e) {
                    Logger.error("Fehler beim Senden der Nachricht: " + e.getMessage());
                    return false;
                }
            } else {
                Logger.error("SERVER: Versuch, eine Nachricht zu senden, aber die Session ist bereits geschlossen.");
                return false;
            }
        }
    }
}

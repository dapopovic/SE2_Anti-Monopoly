package at.aau.anti_mon.server.websocketclient;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * This class is responsible for handling the WebSocket communication on the client side (for Tests).
 */
public class WebSocketHandlerClientImpl implements WebSocketHandler {
    private final BlockingQueue<String> messagesQueue;

    public WebSocketHandlerClientImpl(BlockingQueue<String> receivedMessagesQueue){
        messagesQueue = receivedMessagesQueue;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        Logger.info("Connection established with session ID: " + session.getId());
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) throws JsonProcessingException {
        String json = (String) message.getPayload();
        Logger.info("CLIENT : Nachricht empfangen: " + json);
        JsonDataDTO jsonDataDTO = JsonDataUtility.parseJsonMessage(json);
        Commands command = jsonDataDTO.getCommand();

        if (command != null) {
            try {

                JsonDataDTO  data = JsonDataUtility.parseJsonMessage(json);
                Commands commands = data.getCommand();

                Logger.info("CLIENT : Command: " + commands.getCommand());
                Logger.info("CLIENT : Data: " + data);

                switch (commands) {
                    case PIN -> messagesQueue.add(json);
                    case HEARTBEAT -> Logger.info("CLIENT : Heartbeat received");
                    default -> Logger.error("CLIENT :  Unbekannter oder nicht unterstützter Befehl: " + commands);
                }
            } catch (JsonSyntaxException e) {
                Logger.error("CLIENT : Fehler beim Parsen der JSON-Nachricht: " + json);
            }
        } else { // Wenn kein Befehl vorhanden ist
            Logger.error("CLIENT : Kein Befehl in der Nachricht: " + json);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, @NotNull Throwable exception) {
        Logger.error("Transport error in session ID: " + session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus closeStatus){
        Logger.info("Connection closed with session ID: " + session.getId() + "; Close status: " + closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

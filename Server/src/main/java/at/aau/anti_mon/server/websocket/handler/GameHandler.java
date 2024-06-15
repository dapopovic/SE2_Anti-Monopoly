package at.aau.anti_mon.server.websocket.handler;

import at.aau.anti_mon.server.commands.Command;
import at.aau.anti_mon.server.commands.CommandFactory;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.*;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import at.aau.anti_mon.server.utilities.StringUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.tinylog.Logger;
import java.net.InetSocketAddress;
import java.net.URI;


/**
 * This class handles incoming WebSocket messages and delegates them to the appropriate service.
 */
@Component
public class GameHandler implements WebSocketHandler {

    private final  ApplicationEventPublisher eventPublisher;
    private final CommandFactory gameCommandFactory;
    String remoteaddressisnull = "RemoteAddress ist null";

    @Autowired
    public GameHandler(
                       ApplicationEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
        Logger.info("SERVER : GameHandler created");
        this.gameCommandFactory = new CommandFactory(eventPublisher);
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) throws JsonProcessingException {
        Logger.info("SERVER : handleMessage called from session: " + session.getId() + " with payload: " + message.getPayload());

        JsonDataDTO jsonDataDTO = JsonDataUtility.parseJsonMessage(message.getPayload().toString(), JsonDataDTO.class);
        Logger.info("SERVER : Command: " + gameCommandFactory.getCommand(jsonDataDTO.getCommand().getCommand()));
        Command command = gameCommandFactory.getCommand(jsonDataDTO.getCommand().getCommand());
       
        if (command == null) {
            Logger.error("SERVER : Unbekannter oder nicht unterstützter Befehl: " + jsonDataDTO.getCommand().getCommand());
            throw new IllegalArgumentException("Unbekannter oder nicht unterstützter Befehl: " + jsonDataDTO.getCommand().getCommand());
        }
        command.execute(session, jsonDataDTO);
    }



    /**
     * Diese Methode wird aufgerufen, wenn ein Transportfehler auftritt.
     * Sie schließt die WebSocket-Sitzung und entfernt sie aus der Session-Verwaltung.
     * @param session WebSocket-Sitzung
     * @param exception Ausnahme
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        Logger.error("Transportfehler in Session " + session.getId() + ": " + exception.getMessage());
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR.withReason(exception.getMessage()));
        }

        eventPublisher.publishEvent(new SessionDisconnectEvent(session
        //        ,userID
        ));
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung hergestellt wird.
     * @param session WebSocket-Sitzung
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        //////////////////////////////////////////////////////////////////// DEBUG
        InetSocketAddress clientAddress = session.getRemoteAddress();
        HttpHeaders handshakeHeaders = session.getHandshakeHeaders();

        if (clientAddress == null) {
            Logger.error(remoteaddressisnull);
        }else {
            Logger.info("Accepted connection from: {}:{}", clientAddress.getHostString(), clientAddress.getPort());
            Logger.debug("Client hostname: {}", clientAddress.getHostName());
            Logger.debug("Client ip: {}", clientAddress.getAddress().getHostAddress());
            Logger.debug("Client port: {}", clientAddress.getPort());
        }

        if (session.getRemoteAddress() == null) {
            Logger.error(remoteaddressisnull);
        }else {
            Logger.debug("Session accepted protocols: {}", session.getAcceptedProtocol());
            Logger.debug("Session binary message size limit: {}", session.getBinaryMessageSizeLimit());
            Logger.debug("Session id: {}", session.getId());
            Logger.debug("Session text message size limit: {}", session.getTextMessageSizeLimit());
            Logger.debug("Handshake header: Accept {}", handshakeHeaders.toString());
        }
        URI sessionUri = session.getUri();

        if (sessionUri == null) {
            Logger.error("URI ist null");
        }else {
            Logger.debug("Session uri: {}", sessionUri.toString());
            String userID = StringUtility.extractUserID(sessionUri.getQuery());
            Logger.info( "Query " + sessionUri.getQuery() );
            Logger.info("Neue WebSocket-Sitzung für UserID {}: {}", userID, session.getId());
        }

        eventPublisher.publishEvent(new SessionConnectEvent(session
        //        ,userID
        ));
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung geschlossen wird.
     * @param session WebSocket-Sitzung
     * @param closeStatus Status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus closeStatus) {
        InetSocketAddress clientAddress = session.getRemoteAddress();
        if (clientAddress == null) {
            Logger.error(remoteaddressisnull);
        }else {
            Logger.info("Connection closed by {}:{}", clientAddress.getHostString(), clientAddress.getPort());
        }
        Logger.info("WebSocket-Sitzung wird geschlossen: " + session.getId());

        URI sessionUri = session.getUri();
        if (sessionUri == null) {
            Logger.error("URI ist null");
        }else {
            Logger.info( "Query " + sessionUri.getQuery() );
        }

        eventPublisher.publishEvent(new SessionDisconnectEvent(session
              //  ,userID
        ));
    }

    /**
     * Diese Methode gibt an, ob die WebSocket-Handler-Implementierung
     * @return true, wenn die Implementierung das Empfangen von Teilmeldungen unterstützt, andernfalls false
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}

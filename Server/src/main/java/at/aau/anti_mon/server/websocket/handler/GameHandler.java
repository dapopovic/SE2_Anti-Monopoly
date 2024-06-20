package at.aau.anti_mon.server.websocket.handler;

import at.aau.anti_mon.server.commands.Command;
import at.aau.anti_mon.server.commands.CommandFactory;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.events.*;
import at.aau.anti_mon.server.service.SessionManagementService;
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


/**
 * This class handles incoming WebSocket messages and delegates them to the appropriate service.
 */
@Component
public class GameHandler implements WebSocketHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final CommandFactory gameCommandFactory;
    private final SessionManagementService sessionManagementService;

    @Autowired
    public GameHandler(
                       ApplicationEventPublisher eventPublisher,
                       CommandFactory gameCommandFactory,
                       SessionManagementService sessionManagementService
    ) {
        this.eventPublisher = eventPublisher;
        this.gameCommandFactory = gameCommandFactory;
        this.sessionManagementService = sessionManagementService;
        Logger.info("SERVER : GameHandler created");
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
     * @throws Exception TODO: Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, @NotNull Throwable exception) throws Exception {
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Unknown error";
        Logger.error("Transportfehler in Session " + session.getId() + ": " + errorMessage, exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR.withReason(errorMessage));
        }
        eventPublisher.publishEvent(new SessionDisconnectEvent(session));
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung hergestellt wird.
     * @param session WebSocket-Sitzung
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        InetSocketAddress clientAddress = session.getRemoteAddress();
        HttpHeaders handshakeHeaders = session.getHandshakeHeaders();

        if (clientAddress != null) {
            Logger.info("Accepted connection from: {}:{}", clientAddress.getHostString(), clientAddress.getPort());
            Logger.debug("Client hostname: {}", clientAddress.getHostName());
            Logger.debug("Client ip: {}", clientAddress.getAddress().getHostAddress());
            Logger.debug("Client port: {}", clientAddress.getPort());

        }else {
            Logger.error("RemoteAddress ist null");
        }

        Logger.debug("Session accepted protocols: {}", session.getAcceptedProtocol());
        Logger.debug("Session binary message size limit: {}", session.getBinaryMessageSizeLimit());
        Logger.debug("Session id: {}", session.getId());
        Logger.debug("Session text message size limit: {}", session.getTextMessageSizeLimit());
        Logger.debug("Handshake header: Accept {}", handshakeHeaders.toString());


        if (session.getUri() != null) {
            String userID = StringUtility.extractUserID(session.getUri().getQuery());
            Logger.info("Neue WebSocket-Sitzung für UserID {}: {}", userID, session.getId());
            sessionManagementService.registerUserWithSession(userID, session);
        }else {
            Logger.error("URI ist null");
        }

        eventPublisher.publishEvent(new SessionConnectEvent(session));
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung geschlossen wird.
     * @param session WebSocket-Sitzung
     * @param closeStatus Status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus closeStatus) {
        InetSocketAddress clientAddress = session.getRemoteAddress();
        if (clientAddress != null) {
            Logger.info("Connection closed by {}:{}", clientAddress.getHostString(), clientAddress.getPort());
        } else {
            Logger.error("RemoteAddress ist null");
        }

        Logger.info("WebSocket-Sitzung wird geschlossen: " + session.getId());

        if (session.getUri() != null) {
            Logger.info( "Query " + session.getUri().getQuery() );
            //String userID = StringUtility.extractUserID(session.getUri().getQuery());
            //sessionManagementService.removeSessionById(session.getId(), userID);
        }else {
            Logger.error("URI ist null");
        }

        eventPublisher.publishEvent(new SessionDisconnectEvent(session));
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

package at.aau.anti_mon.server.websocket.handler;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.CreateLobbyEvent;
import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.events.SessionDisconnectEvent;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.game.Player;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.tinylog.Logger;

/**
 * Diese Klasse implementiert die WebSocketHandler-Schnittstelle und behandelt eingehende WebSocket-Nachrichten.
 */
@Component
public class GameHandler implements WebSocketHandler {

    private final LobbyService lobbyService;
    private final SessionManagementService sessionManagementService;
    private final  ApplicationEventPublisher eventPublisher;

    @Autowired
    public GameHandler(LobbyService lobbyService,
                       SessionManagementService sessionManagementService,
                       ApplicationEventPublisher eventPublisher) {
        this.lobbyService = lobbyService;
        this.sessionManagementService = sessionManagementService;
        this.eventPublisher = eventPublisher;
    }


    /**
     * Diese Methode behandelt eingehende WebSocket-Nachrichten.
     * @param session WebSocket-Sitzung
     * @param message WebSocket-Nachricht
     * @throws Exception  TODO: Exception
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Logger.info("handleMessage called from session: " + session.getId() + " with payload: " + message.getPayload());
        ObjectMapper mapper = new ObjectMapper();
        String json = message.getPayload().toString();
        JsonNode rootNode = mapper.readTree(message.getPayload().toString());
        Logger.info("Nachricht empfangen: " + json);

        if (rootNode.has("command")) {

            try {
                JsonDataDTO data = mapper.readValue(json, JsonDataDTO.class);
                Commands commands = data.getCommand();

                Logger.info("Command: " + commands.getCommand());
                Logger.info("Data: " + data);

                switch (commands) {
                    case CREATE_GAME -> {
                        // data = username
                        String playerName = data.getData().get("name");
                        if (playerName != null) {
                            Player player = new Player(playerName, session);
                            eventPublisher.publishEvent(new CreateLobbyEvent(session, player));
                        } else {
                            Logger.error("JSON 'data' ist null oder 'name' ist nicht vorhanden.");
                        }
                        break;
                    }
                    case JOIN_GAME -> {
                        // data = {"pin": 1234 , "name": "Test"}
                        String pinString = data.getData().get("pin");
                        String playerName = data.getData().get("name");
                        if (pinString != null && playerName != null) {
                            int pin = Integer.parseInt(pinString);
                            Player player = new Player(playerName, session);
                            lobbyService.findLobbyByPin(pin).ifPresent(lobby -> {
                                eventPublisher.publishEvent(new UserJoinedLobbyEvent(session, lobby, player));
                            });
                        } else {
                            Logger.error("Erforderliche Daten für 'JOIN_GAME' fehlen.");
                        }
                        break;
                    }
                    default -> {
                        Logger.error("Unbekannter oder nicht unterstützter Befehl: " + commands);
                        break;
                    }
                }
            } catch (JsonSyntaxException e) {
                Logger.error("Fehler beim Parsen der JSON-Nachricht: " + json);
            }
        } else {
            Logger.error("JSON-Nachricht enthält kein 'command'-Attribut.");
        }
    }


    /**
     * Diese Methode wird aufgerufen, wenn ein Transportfehler auftritt.
     * Sie schließt die WebSocket-Sitzung und entfernt sie aus der Session-Verwaltung.
     * @param session WebSocket-Sitzung
     * @param exception Ausnahme
     * @throws Exception TODO: Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR.withReason("Transport error"));
        }
        //sessionManagementService.removeSession(session);
        eventPublisher.publishEvent(new SessionDisconnectEvent(session));
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung hergestellt wird.
     * @param session WebSocket-Sitzung
     * @throws Exception  TODO: Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Logger.info("Neue WebSocket-Sitzung: " + session.getId());
        //sessionManagementService.registerSession(session);
        eventPublisher.publishEvent(new SessionConnectEvent(session));
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine WebSocket-Sitzung geschlossen wird.
     * @param session WebSocket-Sitzung
     * @param closeStatus Status
     * @throws Exception  TODO: Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        Logger.info("WebSocket-Sitzung geschlossen: " + session.getId());
        //sessionManagementService.removeSession(session);
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





    /* OLD Code  / Backup
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Gson gson = new Gson();
        String json = message.getPayload().toString();
        Command command = gson.fromJson(json, Command.class);
        JsonObject data = command.getData();
        switch (command.getCommand()) {
            case CREATE_GAME -> {
                Lobby lobby = new Lobby();
                Player player = gson.fromJson(data, Player.class);
                player.setSession(session);
                lobby.addPlayer(player);
                lobbies.add(lobby);
                TextMessage response = new TextMessage(Integer.toString(lobby.getPin()));
                session.sendMessage(response);
            }
            case JOIN_GAME -> {
                int pin = data.get("pin").getAsInt();
                Player player = gson.fromJson(data, Player.class);
                player.setSession(session);
                Lobby lobby = findLobbyByPin(pin);
                if (lobby != null) {
                    lobby.addPlayer(player);
                    notifyPlayersInLobby(lobby);
                } else {
                    session.sendMessage(new TextMessage("Fehler: Lobby voll oder nicht gefunden."));
                }
            }
        }
    }
     */

}

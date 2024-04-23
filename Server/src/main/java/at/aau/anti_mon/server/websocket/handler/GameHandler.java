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
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.tinylog.Logger;

/**
 * This class handles incoming WebSocket messages and delegates them to the appropriate service.
 */
@Component
public class GameHandler implements WebSocketHandler {

    private final LobbyService lobbyService;
    //private final SessionManagementService sessionManagementService;
    private final  ApplicationEventPublisher eventPublisher;

    @Autowired
    public GameHandler(LobbyService lobbyService,
                       //SessionManagementService sessionManagementService,
                       ApplicationEventPublisher eventPublisher) {
        this.lobbyService = lobbyService;
        //this.sessionManagementService = sessionManagementService;
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
        Logger.info("SERVER : handleMessage called from session: " + session.getId() + " with payload: " + message.getPayload());
        String json = message.getPayload().toString();

        Logger.info("SERVER : Nachricht empfangen: " + json);
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json);
        Commands command = jsonDataDTO.getCommand();


        // Todo: Ersetzen ähnlich wie im Frontend mit CommandFactory
        if (command != null) {
            try {
                Logger.info("SERVER : Command: " + command.getCommand());
                Logger.info("SERVER : Data: " + jsonDataDTO.getData());

                switch (command) {
                    case CREATE_GAME -> {
                        // data = {"name": "username" }
                        String playerName = jsonDataDTO.getData().get("username");
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
                        String pinString = jsonDataDTO.getData().get("pin");
                        String playerName = jsonDataDTO.getData().get("username");

                        if (pinString != null && playerName != null) {
                            int pin = Integer.parseInt(pinString);
                            Player player = new Player(playerName, session);
                            lobbyService.findLobbyByPin(pin).ifPresent(lobby -> {
                                eventPublisher.publishEvent(new UserJoinedLobbyEvent(session, lobby, player));
                            });
                        } else {
                            Logger.error("SERVER : Erforderliche Daten für 'JOIN_GAME' fehlen.");
                        }
                        break;
                    }
                    default -> {
                        Logger.error("SERVER : Unbekannter oder nicht unterstützter Befehl: " + command);
                        break;
                    }
                }
            } catch (JsonSyntaxException e) {
                Logger.error("SERVER : Fehler beim Parsen der JSON-Nachricht: " + json);
            }
        } else {
            Logger.error("SERVER : JSON-Nachricht enthält kein 'command'-Attribut.");
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

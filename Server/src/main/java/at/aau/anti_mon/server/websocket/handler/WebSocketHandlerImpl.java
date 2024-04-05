package at.aau.anti_mon.server.websocket.handler;

import at.aau.anti_mon.server.game.Command;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.Player;
import com.google.gson.Gson;
import org.springframework.web.socket.*;

import java.util.ArrayList;

public class WebSocketHandlerImpl implements WebSocketHandler {
    ArrayList<Lobby> lobbies = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Gson gson = new Gson();
        String json = message.getPayload().toString();
        Command command = gson.fromJson(json, Command.class);
        String data = command.getData();
        switch (command.getCommand()) {
            case CREATE_GAME -> {
                Lobby lobby = new Lobby();
                lobby.addPlayer(new Player(data, session));
                lobbies.add(lobby);
                TextMessage response = new TextMessage(Integer.toString(lobby.getPin()));
                session.sendMessage(response);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        for (Lobby lobby : lobbies) {
            Player player = lobby.getPlayerWithSession(session);
            if (player != null) {
                lobby.removePlayer(player);
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

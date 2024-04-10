package at.aau.anti_mon.server.websocket.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager {
    private static final Map<String, Integer> userLobbyMap = new ConcurrentHashMap<>();

    public static void joinLobby(String userId, Integer lobbyId) {
        userLobbyMap.put(userId, lobbyId);
    }

    public static void leaveLobby(String userId) {
        userLobbyMap.remove(userId);
    }

    public static Integer getLobbyByUserId(String userId) {
        return userLobbyMap.get(userId);
    }
}
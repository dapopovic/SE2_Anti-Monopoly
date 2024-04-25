package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LobbyServiceUnitTest {

    @Test
    public void addUserToLobbyShouldAddUserToLobby() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);

        lobbyService.addUserToLobby("user1", 1234);

        assertEquals(1234, lobbyService.getLobbyIDForUserID("user1"));
    }

    @Test
    public void removeUserFromLobbyShouldRemoveUserFromLobby() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);

        lobbyService.addUserToLobby("user1", 1234);
        lobbyService.removeUserFromLobby("user1");

        assertNull(lobbyService.getLobbyIDForUserID("user1"));
    }

    @Test
    public void createLobbyShouldCreateNewLobby() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user = new User("user1", session);

        Lobby lobby = lobbyService.createLobby(user);

        assertNotNull(lobby);
        assertEquals(user, lobby.getOwner());
    }

    @Test
    public void joinLobbyShouldAddUserToLobby() throws UserNotFoundException, LobbyIsFullException, LobbyNotFoundException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        User user2 = userService.findOrCreateUser("user2", session);
        Lobby lobby = lobbyService.createLobby(user1);

        lobbyService.joinLobby(lobby.getPin(), "user2");

        assertTrue(lobby.getUsers().stream().anyMatch(u -> u.getName().equals("user2")));
    }

    @Test
    public void leaveLobbyShouldRemoveUserFromLobby() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        User user2 = userService.findOrCreateUser("user2", session);

        Lobby lobby = lobbyService.createLobby(user1);
        lobbyService.joinLobby(lobby.getPin(), "user2");
        lobbyService.leaveLobby(lobby.getPin(), "user2");

        assertFalse(lobby.getUsers().stream().anyMatch(u -> u.getName().equals("user2")));
    }
}
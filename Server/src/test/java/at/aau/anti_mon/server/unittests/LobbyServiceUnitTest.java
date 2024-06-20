package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.enums.GameStateEnum;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for the LobbyService
 */
class LobbyServiceUnitTest {

    @Test
    void addUserToLobbyShouldAddUserToLobby() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);
        lobbyService.addUserToLobby("user1", 1234);
        assertEquals(1234, lobbyService.getLobbyIDForUserID("user1").get());
    }

    @Test
    void removeUserFromLobbyShouldRemoveUserFromLobby() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);
        lobbyService.addUserToLobby("user1", 1234);
        lobbyService.removeUserFromLobby("user1");
        assertNull(lobbyService.getLobbyIDForUserID("user1").orElse(null));
    }

    @Test
    void findUserinAllLobbiesShouldReturnUser() throws UserNotFoundException {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);
        Lobby lobby = mock(Lobby.class);
        when(lobby.getPin()).thenReturn(1234);
        WebSocketSession session = mock(WebSocketSession.class);
        User user = new User("user1", session);
        when(session.getId()).thenReturn("session1");
        lobbyService.createLobby(user);
        assertEquals(user, lobbyService.findUserInAllLobbies("user1"));
    }

    @Test
    void findUserinAllLobbiesShouldThrowException() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);
        assertThrows(UserNotFoundException.class, () -> lobbyService.findUserInAllLobbies("user2"));
    }

    @Test
    void findOptionalLobbyByPinReturnsLobby() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        User user = new User("user1", session);
        when(session.getId()).thenReturn("session1");
        Lobby lobby = lobbyService.createLobby(user);
        assertEquals(lobby, lobbyService.findOptionalLobbyByPin(lobby.getPin()).get());
    }

    @Test
    void findOptionalLobbyByPinReturnsNull() {
        UserService userService = mock(UserService.class);
        LobbyService lobbyService = new LobbyService(userService);
        assertNull(lobbyService.findOptionalLobbyByPin(1234).orElse(null));
    }

    @Test
    void createLobbyShouldCreateNewLobby() {
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
    void joinLobbyShouldAddUserToLobby() throws UserNotFoundException, LobbyIsFullException, LobbyNotFoundException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        userService.findOrCreateUser("user2", session);
        Lobby lobby = lobbyService.createLobby(user1);
        lobbyService.joinLobby(lobby.getPin(), "user2");
        assertTrue(lobby.getUsers().stream().anyMatch(u -> u.getName().equals("user2")));
    }

    @Test
    void leaveLobbyShouldRemoveUserFromLobby() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        userService.findOrCreateUser("user2", session);
        Lobby lobby = lobbyService.createLobby(user1);
        lobbyService.joinLobby(lobby.getPin(), "user2");
        lobbyService.leaveLobby(lobby.getPin(), "user2");
        assertFalse(lobby.getUsers().stream().anyMatch(u -> u.getName().equals("user2")));
    }
    @Test
    void readyUserShouldSetUserReady() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        Lobby lobby = lobbyService.createLobby(user1);
        userService.findOrCreateUser("user2", session);
        lobbyService.joinLobby(lobby.getPin(), "user2");
        lobbyService.readyUser(lobby.getPin(), "user2");

        assertTrue(lobby.getUsers().stream().anyMatch(u -> u.getName().equals("user2") && u.isReady()));
    }
    @Test
    void startGameShouldStartGame() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        Lobby lobby = lobbyService.createLobby(user1);
        userService.findOrCreateUser("user2", session);
        lobbyService.joinLobby(lobby.getPin(), "user2");
        lobbyService.readyUser(lobby.getPin(), "user2");

        lobbyService.startGame(lobby.getPin(), "user1");

        assertEquals(GameStateEnum.INGAME, lobby.getGameState());
    }
    @Test
    void startGameWithoutAllUsersReadyShouldStillBeInLobbyState() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        Lobby lobby = lobbyService.createLobby(user1);
        userService.findOrCreateUser("user2", session);
        lobbyService.joinLobby(lobby.getPin(), "user2");
        lobbyService.startGame(lobby.getPin(), "user1");

        assertEquals(GameStateEnum.LOBBY, lobby.getGameState());
    }
    @Test
    void startGameNotStartedByOwnerShouldStillBeInLobbyState() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        LobbyService lobbyService = new LobbyService(userService);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        User user1 = userService.findOrCreateUser("user1", session);
        Lobby lobby = lobbyService.createLobby(user1);
        userService.findOrCreateUser("user2", session);
        lobbyService.joinLobby(lobby.getPin(), "user2");
        lobbyService.readyUser(lobby.getPin(), "user2");

        lobbyService.startGame(lobby.getPin(), "user2");

        assertEquals(GameStateEnum.LOBBY, lobby.getGameState());
    }
}
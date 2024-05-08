package at.aau.anti_mon.server.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketSession;

import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.UserService;

/**
 * Integration tests for the LobbyService
 */
@SpringBootTest
@ActiveProfiles("test")
class LobbyServiceIntegrationTest {

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private UserService userService;

    @Test
    void createLobbyShouldCreateNewLobby() {
        User user = userService.findOrCreateUser("newUser", mock(WebSocketSession.class));
        Lobby lobby = lobbyService.createLobby(user);
        assertNotNull(lobby);
        assertEquals(user, lobby.getOwner());
    }

    @Test
    void joinLobbyShouldAddUserToLobby() throws UserNotFoundException, LobbyIsFullException, LobbyNotFoundException {
        User user = userService.findOrCreateUser("newUser", mock(WebSocketSession.class));
        Lobby lobby = lobbyService.createLobby(user);
        User newUser = userService.findOrCreateUser("userToJoin", mock(WebSocketSession.class));
        lobbyService.joinLobby(lobby.getPin(), newUser.getName());
        assertTrue(lobby.getUsers().contains(newUser));
    }

    @Test
    void leaveLobbyShouldRemoveUserFromLobby()
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        User user = userService.findOrCreateUser("newUser", mock(WebSocketSession.class));
        Lobby lobby = lobbyService.createLobby(user);
        User newUser = userService.findOrCreateUser("userToLeave", mock(WebSocketSession.class));
        lobbyService.joinLobby(lobby.getPin(), newUser.getName());
        lobbyService.leaveLobby(lobby.getPin(), newUser.getName());
        assertFalse(lobby.getUsers().contains(newUser));
    }

    @Test
    void findLobbyByPinShouldReturnLobbyWhenLobbyExists() throws LobbyNotFoundException {
        User user = userService.findOrCreateUser("newUser", mock(WebSocketSession.class));
        Lobby lobby = lobbyService.createLobby(user);
        Lobby foundLobby = lobbyService.findLobbyByPin(lobby.getPin());
        assertEquals(lobby, foundLobby);
    }

    @Test
    void findLobbyByPinShouldThrowExceptionWhenLobbyDoesNotExist() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyService.findLobbyByPin(9999));
    }
}
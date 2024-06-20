package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.UserAlreadyExistsException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Lobby
 */
class LobbyUnitTest {

    private Lobby lobby;
    private User player1;

    @BeforeEach
    void setUp() {
        lobby = new Lobby();
        player1 = new User("player1", mock(WebSocketSession.class));
    }

    private User createUserWithOpenSession(String id) {
        User user = new User(id, mock(WebSocketSession.class));
        when(user.getSession().isOpen()).thenReturn(true);
        when(user.getSession().getId()).thenReturn(id);
        return user;
    }

    @Test
    void testLobbyCapacity() throws LobbyIsFullException {
        for (int i = 0; i < 6; i++) {
            lobby.addUser(createUserWithOpenSession("player" + i));
        }
        assertThrows(LobbyIsFullException.class, () -> lobby.addUser(createUserWithOpenSession("player7")));
    }

    @Test
    void testFindPlayerWithSessionId() throws LobbyIsFullException {
        // User mit gemockter Session
        WebSocketSession playerSession = mock(WebSocketSession.class);
        when(playerSession.getId()).thenReturn("player1");
        User player1 = new User("player1", playerSession);

        lobby.addUser(player1);

        // Weitere Session
        WebSocketSession testSession = mock(WebSocketSession.class);
        when(testSession.getId()).thenReturn("player1");

        Optional<User> optionalUser = lobby.getUserWithSession(testSession);
        optionalUser.ifPresentOrElse(user -> Assertions.assertEquals(player1, user), Assertions::fail);

        // Überprüfe, ob getId() aufgerufen wurde
        verify(testSession).getId();

        // Überprüfe, ob getId() auf dem ursprünglichen Spieler aufgerufen wurde
        verify(playerSession).getId();
    }

    @Test
    void lobbyShouldReturnNullWhenNoUserWithMatchingSession() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("player2");

        Optional<User> optionalUser = lobby.getUserWithSession(session);

        // Extrahieren des Werts (falls vorhanden) oder null
        User user = optionalUser.orElse(null);

        Assertions.assertNull(user, "User should be null");
    }

    @Test
    void lobbyShouldReturnTrueWhenPlayerInLobby() throws LobbyIsFullException {
        User user = createUserWithOpenSession("player2");
        lobby.addUser(user);
        Assertions.assertTrue(lobby.isPlayerInLobby(user));
    }

    @Test
    void lobbyShouldReturnFalseWhenPlayerNotInLobby() {
        User user = createUserWithOpenSession("player2");
        Assertions.assertFalse(lobby.isPlayerInLobby(user));
    }

    @Test
    void lobbyShouldReturnTrueWhenCanAddPlayer() {
        Assertions.assertTrue(lobby.canAddPlayer());
    }

    @Test
    void lobbyShouldReturnFalseWhenCannotAddPlayer() throws LobbyIsFullException {
        for (int i = 0; i < 6; i++) {
            lobby.addUser(createUserWithOpenSession("player" + i));
        }
        Assertions.assertFalse(lobby.canAddPlayer());
    }

    @Test
    void lobbyShouldAddUserWhenNotFull() throws LobbyIsFullException {
        User user = createUserWithOpenSession("player2");
        lobby.addUser(user);
        Assertions.assertEquals(1, lobby.getUsers().size());
        Assertions.assertTrue(lobby.getUsers().contains(user));
    }

    @Test
    void testAddPlayerThrowExceptionBecauseUserAlreadyInLobby()
            throws UserAlreadyExistsException, LobbyIsFullException {
        lobby.addUser(player1);
        assertThrows(UserAlreadyExistsException.class, () -> lobby.addUser(player1));
        Assertions.assertEquals(1, lobby.getUsers().size());
    }

    @Test
    void lobbyShouldRemoveUserWhenPresent() throws LobbyIsFullException, UserNotFoundException {
        User user = createUserWithOpenSession("player2");
        lobby.addUser(user);
        lobby.removeUser(user);
        Assertions.assertEquals(0, lobby.getUsers().size());
        Assertions.assertFalse(lobby.getUsers().contains(user));
    }
    @Test
    void lobbyShouldRemoveOwnerFromLobby() throws LobbyIsFullException, UserNotFoundException {
        User user = createUserWithOpenSession("player2");
        lobby.addUser(player1);
        lobby.setOwner(player1);
        lobby.addUser(user);
        lobby.removeUser(player1);
        Assertions.assertEquals(1, lobby.getUsers().size());
        Assertions.assertFalse(lobby.getUsers().contains(player1));
        Assertions.assertTrue(lobby.getUsers().contains(user));
        Assertions.assertEquals(user, lobby.getOwner());
    }

    @Test
    void lobbyShouldThrowExceptionWhenRemovingUserNotPresent() {
        User user = createUserWithOpenSession("player2");
        assertThrows(UserNotFoundException.class, () -> lobby.removeUser(user));
    }

    @Test
    void testRemovePlayerNotInLobby() {
        when(player1.getSession().isOpen()).thenReturn(true);
        assertThrows(UserNotFoundException.class, () -> lobby.removeUser(player1));
        Assertions.assertEquals(0, lobby.getUsers().size());
        Assertions.assertFalse(lobby.getUsers().contains(player1));
        verify(player1.getSession(), never()).isOpen();
    }

}

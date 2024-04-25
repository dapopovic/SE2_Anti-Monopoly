package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceUnitTest {

    @Test
    public void findOrCreateUserShouldCreateNewUserWhenUserDoesNotExist() {
        UserService userService = new UserService(mock(SessionManagementService.class));
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");

        User user = userService.findOrCreateUser("user1", session);

        assertNotNull(user);
        assertEquals("user1", user.getName());
        assertEquals(session, user.getSession());
    }

    @Test
    public void findOrCreateUserShouldReturnExistingUserWhenUserExists() {
        UserService userService = new UserService(mock(SessionManagementService.class));
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");

        userService.createUser("user1", session);
        User user = userService.findOrCreateUser("user1", session);

        assertNotNull(user);
        assertEquals("user1", user.getName());
        assertEquals(session, user.getSession());
    }

    @Test
    public void getOptionalUserShouldReturnEmptyWhenUserDoesNotExist() {
        UserService userService = new UserService(mock(SessionManagementService.class));

        Optional<User> user = userService.getOptionalUser("user1");

        assertTrue(user.isEmpty());
    }

    @Test
    public void getOptionalUserShouldReturnUserWhenUserExists() {
        UserService userService = new UserService(mock(SessionManagementService.class));
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");

        userService.findOrCreateUser("user1", session);
        Optional<User> user = userService.getOptionalUser("user1");

        assertTrue(user.isPresent());
        assertEquals("user1", user.get().getName());
        assertEquals(session, user.get().getSession());
    }

    @Test
    public void getUserShouldThrowExceptionWhenUserDoesNotExist() {
        UserService userService = new UserService(mock(SessionManagementService.class));

        assertThrows(UserNotFoundException.class, () -> userService.getUser("user1"));
    }

    @Test
    public void getUserShouldReturnUserWhenUserExists() throws UserNotFoundException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");

        userService.findOrCreateUser("user1", session);
        User user = userService.getUser("user1");

        assertNotNull(user);
        assertEquals("user1", user.getName());
        assertEquals(session, user.getSession());
    }

    @Test
    public void removeUserShouldThrowExceptionWhenUserDoesNotExist() {
        UserService userService = new UserService(mock(SessionManagementService.class));

        assertThrows(UserNotFoundException.class, () -> userService.removeUser("user1"));
    }

    @Test
    public void removeUserShouldRemoveUserWhenUserExists() throws UserNotFoundException {
        UserService userService = new UserService(mock(SessionManagementService.class));
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");

        userService.findOrCreateUser("user1", session);
        userService.removeUser("user1");

        assertThrows(UserNotFoundException.class, () -> userService.getUser("user1"));
    }
}
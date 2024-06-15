package at.aau.anti_mon.server.integrationtests;

import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Integration tests for the UserService
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void findOrCreateUserShouldCreateNewUserWhenUserDoesNotExist() {
        WebSocketSession session = mock(WebSocketSession.class);
        User user = userService.findOrCreateUser("newUser", session);
        assertNotNull(user);
        assertEquals("newUser", user.getName());
    }

    @Test
    void findOrCreateUserShouldReturnExistingUserWhenUserExists() {
        WebSocketSession session = mock(WebSocketSession.class);
        userService.findOrCreateUser("existingUser", session);
        User user = userService.findOrCreateUser("existingUser", session);
        assertNotNull(user);
        assertEquals("existingUser", user.getName());
    }

    @Test
    void getOptionalUserShouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> user = userService.getOptionalUser("nonExistingUser");
        assertTrue(user.isEmpty());
    }

    @Test
    void getOptionalUserShouldReturnUserWhenUserExists() {
        WebSocketSession session = mock(WebSocketSession.class);
        userService.findOrCreateUser("existingUser", session);
        Optional<User> user = userService.getOptionalUser("existingUser");
        assertTrue(user.isPresent());
        assertEquals("existingUser", user.get().getName());
    }

    @Test
    void removeUserShouldRemoveExistingUser() throws UserNotFoundException {
        WebSocketSession session = mock(WebSocketSession.class);
        userService.findOrCreateUser("userToRemove", session);
        userService.removeUser("userToRemove");
        Optional<User> user = userService.getOptionalUser("userToRemove");
        assertTrue(user.isEmpty());
    }

    @Test
    void removeUserShouldThrowExceptionWhenUserDoesNotExist() {
        assertThrows(UserNotFoundException.class, () -> userService.removeUser("nonExistingUser"));
    }
}

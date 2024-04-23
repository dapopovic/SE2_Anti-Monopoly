package at.aau.anti_mon.server;

import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class LobbyUnitTest {

    private Lobby lobby;

    @BeforeEach
    public void setUp() {
        lobby = new Lobby();
    }

    @Test
    void testFindPlayerWithSessionId() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        when(player1.getSession().getId()).thenReturn("player1");
        assertDoesNotThrow(() -> lobby.addPlayer(player1));
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("player1");
        Player player = lobby.getPlayerWithSession(session);
        Assertions.assertNotNull(player);
        Assertions.assertEquals(player1, player);
        verify(session).getId();
        verify(player1.getSession()).getId();
    }
    @Test
    void testFindPlayerWithSessionIdNotFound() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        when(player1.getSession().getId()).thenReturn("player1");
        assertDoesNotThrow(() -> lobby.addPlayer(player1));
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("player2");
        Player player = lobby.getPlayerWithSession(session);
        Assertions.assertNull(player);
        verify(session).getId();
        verify(player1.getSession()).getId();
    }
    @Test
    void testAddPlayer() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        assertDoesNotThrow(() -> lobby.addPlayer(player1));
        Assertions.assertEquals(1, lobby.getPlayers().size());
        Assertions.assertTrue(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    void testAddPlayerAlreadyInLobby() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        assertDoesNotThrow(() -> lobby.addPlayer(player1));
        Exception exception = Assertions.assertThrows(Exception.class, () -> lobby.addPlayer(player1));
        Assertions.assertEquals("Spieler ist bereits in der Lobby!", exception.getMessage());
        Assertions.assertEquals(1, lobby.getPlayers().size());
        Assertions.assertTrue(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    void testAddPlayerLobbyFull() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        for (int i = 0; i < 6; i++) {
            Player player = new Player("player" + i, mock(WebSocketSession.class));
            when(player.getSession().isOpen()).thenReturn(true);
            assertDoesNotThrow(() -> lobby.addPlayer(player));
        }
        Exception exception = Assertions.assertThrows(Exception.class, () -> lobby.addPlayer(player1));
        Assertions.assertEquals("Lobby ist voll!", exception.getMessage());
        Assertions.assertEquals(6, lobby.getPlayers().size());
        Assertions.assertFalse(lobby.getPlayers().contains(player1));
    }
    @Test
    void testAddPlayerSessionClosed() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(false);
        Exception exception = Assertions.assertThrows(Exception.class, () -> lobby.addPlayer(player1));
        Assertions.assertEquals("Spieler ist nicht mehr verbunden!", exception.getMessage());
        Assertions.assertEquals(0, lobby.getPlayers().size());
        Assertions.assertFalse(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    void testRemovePlayer() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        assertDoesNotThrow(() -> lobby.addPlayer(player1));
        lobby.removePlayer(player1);
        Assertions.assertEquals(0, lobby.getPlayers().size());
        Assertions.assertFalse(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    void testRemovePlayerNotInLobby() {
        Player player1 = new Player("player1", mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        lobby.removePlayer(player1);
        Assertions.assertEquals(0, lobby.getPlayers().size());
        Assertions.assertFalse(lobby.getPlayers().contains(player1));
        verify(player1.getSession(), never()).isOpen();
    }
}

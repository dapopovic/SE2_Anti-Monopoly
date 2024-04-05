package at.aau.anti_mon.server;

import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LobbyUnitTest {

    private Lobby lobby;

    @BeforeEach
    public void setUp() {
        lobby = new Lobby();
    }

    @Test
    public void testFindPlayerWithSessionId() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        when(player1.getSession().getId()).thenReturn("player1");
        lobby.addPlayer(player1);
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        when(session.getId()).thenReturn("player1");
        Player player = lobby.getPlayerWithSession(session);
        assertNotNull(player);
        assertEquals(player1, player);
        verify(session).getId();
        verify(player1.getSession()).getId();
    }
    @Test
    public void testFindPlayerWithSessionIdNotFound() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        when(player1.getSession().getId()).thenReturn("player1");
        lobby.addPlayer(player1);
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        when(session.getId()).thenReturn("player2");
        Player player = lobby.getPlayerWithSession(session);
        assertNull(player);
        verify(session).getId();
        verify(player1.getSession()).getId();
    }
    @Test
    public void testAddPlayer() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        lobby.addPlayer(player1);
        assertEquals(1, lobby.getPlayers().size());
        assertTrue(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    public void testAddPlayerAlreadyInLobby() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        lobby.addPlayer(player1);
        lobby.addPlayer(player1);
        assertEquals(1, lobby.getPlayers().size());
        assertTrue(lobby.getPlayers().contains(player1));
        verify(player1.getSession(), times(2)).isOpen();
    }
    @Test
    public void testAddPlayerLobbyFull() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        for (int i = 0; i < 6; i++) {
            Player player = new Player("player" + i, Mockito.mock(WebSocketSession.class));
            when(player.getSession().isOpen()).thenReturn(true);
            lobby.addPlayer(player);
        }
        lobby.addPlayer(player1);
        assertEquals(6, lobby.getPlayers().size());
        assertFalse(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    public void testAddPlayerSessionClosed() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(false);
        lobby.addPlayer(player1);
        assertEquals(0, lobby.getPlayers().size());
        assertFalse(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    public void testRemovePlayer() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        lobby.addPlayer(player1);
        lobby.removePlayer(player1);
        assertEquals(0, lobby.getPlayers().size());
        assertFalse(lobby.getPlayers().contains(player1));
        verify(player1.getSession()).isOpen();
    }
    @Test
    public void testRemovePlayerNotInLobby() {
        Player player1 = new Player("player1", Mockito.mock(WebSocketSession.class));
        when(player1.getSession().isOpen()).thenReturn(true);
        lobby.removePlayer(player1);
        assertEquals(0, lobby.getPlayers().size());
        assertFalse(lobby.getPlayers().contains(player1));
        verify(player1.getSession(), never()).isOpen();
    }
}

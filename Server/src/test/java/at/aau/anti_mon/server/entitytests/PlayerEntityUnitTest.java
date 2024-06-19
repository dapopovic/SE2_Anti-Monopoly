package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.entities.Player;
import at.aau.anti_mon.server.enums.PlayerFigure;
import at.aau.anti_mon.server.enums.PlayerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerEntityUnitTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player.Builder().build();
    }

    @Test
    void builderSetsDefaultValues() {
        assertEquals("TestPlayer", player.getName());
        assertEquals(1500, player.getBalance());
        assertEquals(0, player.getPosition());
        assertFalse(player.isInJail());
        assertNull(player.getGame());
        assertEquals(PlayerFigure.CAR, player.getPlayerFigure());
        assertEquals(PlayerRole.ANTI_MONOPOLIST, player.getPlayerRole());
    }

    @Test
    void builderSetsProvidedValues() {
        Player customPlayer = new Player.Builder()
                .withName("CustomPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        assertEquals("CustomPlayer", customPlayer.getName());
        assertEquals(2000, customPlayer.getBalance());
        assertEquals(5, customPlayer.getPosition());
        assertTrue(customPlayer.isInJail());
        assertEquals(PlayerFigure.SHIP, customPlayer.getPlayerFigure());
        assertEquals(PlayerRole.MONOPOLIST, customPlayer.getPlayerRole());
    }

    @Test
    void builderDoesNotAllowNegativeBalance() {
        Player.Builder builder = new Player.Builder();
        assertThrows(IllegalArgumentException.class, () -> builder.withBalance(-1));
    }

    @Test
    void builderDoesNotAllowNegativePosition() {
        Player.Builder builder = new Player.Builder();
        assertThrows(IllegalArgumentException.class, () -> builder.withPosition(-1));
    }
}
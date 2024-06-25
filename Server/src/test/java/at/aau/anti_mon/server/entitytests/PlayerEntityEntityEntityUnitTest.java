package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.entities.PlayerEntity;
import at.aau.anti_mon.server.enums.PlayerFigure;
import at.aau.anti_mon.server.enums.PlayerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerEntityEntityEntityUnitTest {

    private PlayerEntity playerEntity;

    @BeforeEach
    void setUp() {
        playerEntity = new PlayerEntity.Builder().build();
    }

    @Test
    void builderSetsDefaultValues() {
        assertEquals("TestPlayer", playerEntity.getName());
        assertEquals(1500, playerEntity.getBalance());
        assertEquals(0, playerEntity.getPosition());
        assertFalse(playerEntity.isInJail());
        assertNull(playerEntity.getGame());
        assertEquals(PlayerFigure.CAR, playerEntity.getPlayerFigure());
        assertEquals(PlayerRole.ANTI_MONOPOLIST, playerEntity.getPlayerRole());
    }

    @Test
    void builderSetsProvidedValues() {
        PlayerEntity customPlayerEntityEntity = new PlayerEntity.Builder()
                .withName("CustomPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        assertEquals("CustomPlayer", customPlayerEntityEntity.getName());
        assertEquals(2000, customPlayerEntityEntity.getBalance());
        assertEquals(5, customPlayerEntityEntity.getPosition());
        assertTrue(customPlayerEntityEntity.isInJail());
        assertEquals(PlayerFigure.SHIP, customPlayerEntityEntity.getPlayerFigure());
        assertEquals(PlayerRole.MONOPOLIST, customPlayerEntityEntity.getPlayerRole());
    }

    @Test
    void builderDoesNotAllowNegativeBalance() {
        PlayerEntity.Builder builder = new PlayerEntity.Builder();
        assertThrows(IllegalArgumentException.class, () -> builder.withBalance(-1));
    }

    @Test
    void builderDoesNotAllowNegativePosition() {
        PlayerEntity.Builder builder = new PlayerEntity.Builder();
        assertThrows(IllegalArgumentException.class, () -> builder.withPosition(-1));
    }
}
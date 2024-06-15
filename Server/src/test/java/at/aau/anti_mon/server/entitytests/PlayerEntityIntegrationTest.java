package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.entities.Player;
import at.aau.anti_mon.server.enums.PlayerFigure;
import at.aau.anti_mon.server.enums.PlayerRole;
import at.aau.anti_mon.server.service.PlayerService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PlayerEntityIntegrationTest {

    @Autowired
    private PlayerService playerService;

    @Test
    void testCreatePlayer() {
        Player player = new Player.Builder()
                .withName("TestPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        Player savedPlayer = playerService.createPlayer(player);

        assertNotNull(savedPlayer);
        assertEquals("TestPlayer", savedPlayer.getName());
        assertEquals(2000, savedPlayer.getBalance());
        assertEquals(5, savedPlayer.getPosition());
        assertTrue(savedPlayer.isInJail());
        assertEquals(PlayerFigure.SHIP, savedPlayer.getPlayerFigure());
        assertEquals(PlayerRole.MONOPOLIST, savedPlayer.getPlayerRole());
    }

    @Test
    void createPlayerThrowsConstrainedValidationViolationException() {
        Player player = new Player.Builder()
                .withName("IntegrationTestPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        assertThrows(ConstraintViolationException.class, () -> playerService.createPlayer(player));
    }

}
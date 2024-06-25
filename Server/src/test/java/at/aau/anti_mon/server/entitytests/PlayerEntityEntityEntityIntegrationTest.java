package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.entities.PlayerEntity;
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
class PlayerEntityEntityEntityIntegrationTest {

    @Autowired
    private PlayerService playerService;

    @Test
    void testCreatePlayer() {
        PlayerEntity playerEntity = new PlayerEntity.Builder()
                .withName("TestPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        PlayerEntity savedPlayerEntityEntity = playerService.createPlayer(playerEntity);

        assertNotNull(savedPlayerEntityEntity);
        assertEquals("TestPlayer", savedPlayerEntityEntity.getName());
        assertEquals(2000, savedPlayerEntityEntity.getBalance());
        assertEquals(5, savedPlayerEntityEntity.getPosition());
        assertTrue(savedPlayerEntityEntity.isInJail());
        assertEquals(PlayerFigure.SHIP, savedPlayerEntityEntity.getPlayerFigure());
        assertEquals(PlayerRole.MONOPOLIST, savedPlayerEntityEntity.getPlayerRole());
    }

    @Test
    void createPlayerThrowsConstrainedValidationViolationException() {
        PlayerEntity playerEntity = new PlayerEntity.Builder()
                .withName("IntegrationTestPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        assertThrows(ConstraintViolationException.class, () -> playerService.createPlayer(playerEntity));
    }

}
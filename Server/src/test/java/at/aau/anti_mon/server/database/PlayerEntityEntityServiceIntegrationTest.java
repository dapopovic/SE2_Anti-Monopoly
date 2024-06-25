package at.aau.anti_mon.server.database;

import at.aau.anti_mon.server.entities.PlayerEntity;
import at.aau.anti_mon.server.enums.PlayerFigure;
import at.aau.anti_mon.server.enums.PlayerRole;
import at.aau.anti_mon.server.service.PlayerService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("databasetests")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlayerEntityEntityServiceIntegrationTest extends TestDatabase {

    @Autowired
    private PlayerService playerService;

    @Test
    @Transactional
    void testPlayerCreation() {
        PlayerEntity playerEntity = new PlayerEntity.Builder()
                .withName("Player1")
                .withBalance(777)
                .withPosition(7)
                .withInJail(false)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        playerService.createPlayer(playerEntity);
        List<PlayerEntity> playerEntityEntities = playerService.getAllPlayer();
        assertThat(playerEntityEntities).contains(playerEntity);
    }

    @Test
    void updatePlayerSuccessfully() {
        PlayerEntity existingPlayerEntityEntity = new PlayerEntity.Builder()
                .withName("ExistingPlayer")
                .withBalance(999)
                .withPosition(0)
                .withInJail(false)
                .withPlayerFigure(PlayerFigure.CAR)
                .withPlayerRole(PlayerRole.ANTI_MONOPOLIST)
                .build();

        playerService.createPlayer(existingPlayerEntityEntity);
        Integer existingPlayerID = existingPlayerEntityEntity.getPlayerID();

        PlayerEntity newPlayerEntityEntity = new PlayerEntity.Builder()
                .withName("NewPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        PlayerEntity updatedPlayerEntityEntity = playerService.updatePlayer(existingPlayerID, newPlayerEntityEntity);

        Assertions.assertEquals(newPlayerEntityEntity.getName(), updatedPlayerEntityEntity.getName());
        Assertions.assertEquals(newPlayerEntityEntity.getBalance(), updatedPlayerEntityEntity.getBalance());
        Assertions.assertEquals(newPlayerEntityEntity.getPosition(), updatedPlayerEntityEntity.getPosition());
        Assertions.assertTrue(updatedPlayerEntityEntity.isInJail());
        Assertions.assertEquals(PlayerFigure.SHIP, updatedPlayerEntityEntity.getPlayerFigure());
        Assertions.assertEquals(PlayerRole.MONOPOLIST, updatedPlayerEntityEntity.getPlayerRole());
    }


    @Test
    void updatePlayerWithNonExistingNameReturnsNull() {
        PlayerEntity newPlayerEntityEntity = new PlayerEntity.Builder()
                .withName("NewPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        PlayerEntity updatedPlayerEntityEntity = playerService.updatePlayer(123, newPlayerEntityEntity);

        Assertions.assertNull(updatedPlayerEntityEntity);
    }
}

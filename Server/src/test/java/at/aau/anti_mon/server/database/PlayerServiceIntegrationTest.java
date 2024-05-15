package at.aau.anti_mon.server.database;

import at.aau.anti_mon.server.entities.Player;
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
public class PlayerServiceIntegrationTest extends TestDatabase {

    @Autowired
    private PlayerService playerService;

    @Test
    @Transactional
    public void testPlayerCreation() {
        Player player = new Player.Builder()
                .withName("Player1")
                .withBalance(777)
                .withPosition(7)
                .withInJail(false)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        playerService.createPlayer(player);
        List<Player> players = playerService.getAllPlayer();
        assertThat(players).contains(player);
    }

    @Test
    public void updatePlayerSuccessfully() {
        Player existingPlayer = new Player.Builder()
                .withName("ExistingPlayer")
                .withBalance(999)
                .withPosition(0)
                .withInJail(false)
                .withPlayerFigure(PlayerFigure.CAR)
                .withPlayerRole(PlayerRole.ANTI_MONOPOLIST)
                .build();

        playerService.createPlayer(existingPlayer);
        Integer existingPlayerID = existingPlayer.getPlayerID();

        Player newPlayer = new Player.Builder()
                .withName("NewPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        Player updatedPlayer = playerService.updatePlayer(existingPlayerID, newPlayer);

        Assertions.assertEquals(newPlayer.getName(), updatedPlayer.getName());
        Assertions.assertEquals(newPlayer.getBalance(), updatedPlayer.getBalance());
        Assertions.assertEquals(newPlayer.getPosition(), updatedPlayer.getPosition());
        Assertions.assertTrue(updatedPlayer.isInJail());
        Assertions.assertEquals(PlayerFigure.SHIP, updatedPlayer.getPlayerFigure());
        Assertions.assertEquals(PlayerRole.MONOPOLIST, updatedPlayer.getPlayerRole());
    }


    @Test
    public void updatePlayerWithNonExistingNameReturnsNull() {
        Player newPlayer = new Player.Builder()
                .withName("NewPlayer")
                .withBalance(2000)
                .withPosition(5)
                .withInJail(true)
                .withPlayerFigure(PlayerFigure.SHIP)
                .withPlayerRole(PlayerRole.MONOPOLIST)
                .build();

        Player updatedPlayer = playerService.updatePlayer(123, newPlayer);

        Assertions.assertNull(updatedPlayer);
    }
}

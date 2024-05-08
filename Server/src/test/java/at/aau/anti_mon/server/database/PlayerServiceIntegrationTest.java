package at.aau.anti_mon.server.database;

import at.aau.anti_mon.server.entities.Player;
import at.aau.anti_mon.server.service.PlayerService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestEntityManager
public class PlayerServiceIntegrationTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    @Transactional
    public void testPlayerCreation() {
        Player player = new Player();
        entityManager.persist(player);
        entityManager.flush();

        playerService.createPlayer(player);

        List<Player> players = playerService.getAllPlayer();
        assertThat(players).hasSize(1);
        assertThat(players.get(0).getPlayerID()).isEqualTo(player.getPlayerID());
    }
}
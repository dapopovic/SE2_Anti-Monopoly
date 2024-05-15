package at.aau.anti_mon.server.database;

import at.aau.anti_mon.server.dao.PlayerDAO;
import at.aau.anti_mon.server.entities.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test class for the PlayerRepository
 * The test uses a postgresql test-container to run the tests, loaded by DatabaseTest
 */
@DataJpaTest
@ActiveProfiles("databasetests")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlayerRepositoryTest extends TestDatabase {

    @Autowired
    private PlayerDAO playerDAO;


    @Test
    void testConnectionToDatabase() {
        Assertions.assertNotNull(playerDAO);
    }

    @Test
    public void testFindPlayerByID() {
        Player player = new Player.Builder().build();
        playerDAO.save(player);

        Player found = playerDAO.findById(player.getPlayerID()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isNotNull();
        assertThat(found.getName()).isEqualTo(player.getName());
        assertThat(found.getBalance()).isEqualTo(player.getBalance());
        assertThat(found.getPosition()).isEqualTo(player.getPosition());
    }
}
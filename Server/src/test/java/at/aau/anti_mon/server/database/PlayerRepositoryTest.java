package at.aau.anti_mon.server.database;

import at.aau.anti_mon.server.dao.PlayerDAO;
import at.aau.anti_mon.server.entities.Player;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
//@Transactional // Wird unbedingt benötigt, um die Transaktionsverwaltung zu steuern
@AutoConfigureTestEntityManager
public class PlayerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerDAO playerDAO;

    @Test
    @Transactional
    public void whenFindById_thenReturnPlayer() {
        // gegeben
        Player player = new PlayerBuilder().build();
        entityManager.persist(player);
        entityManager.flush();

        // wenn
        Player found = playerDAO.findById(player.getPlayerID()).orElse(null);

        // dann
        assertThat(found).isNotNull();
        assertThat(found.getName()).isNotNull();
        assertThat(found.getName()).isEqualTo(player.getName());
        assertThat(found.getBalance()).isEqualTo(player.getBalance());
        assertThat(found.getPosition()).isEqualTo(player.getPosition());
    }
}
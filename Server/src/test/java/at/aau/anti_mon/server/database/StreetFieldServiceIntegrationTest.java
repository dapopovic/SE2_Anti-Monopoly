package at.aau.anti_mon.server.database;

import at.aau.anti_mon.server.entities.StreetField;
import at.aau.anti_mon.server.service.StreetFieldService;
import jakarta.transaction.Transactional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("databasetests")
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StreetFieldServiceIntegrationTest extends TestDatabase{

    @Autowired
    private StreetFieldService streetFieldService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void testStreetFieldCreation() {
        StreetField streetField = new StreetField.Builder().build();
       entityManager.persist(streetField);
       entityManager.flush();

        streetFieldService.createGameField(streetField);

        List<StreetField> streetFields = streetFieldService.getAllStreetFields();
        assertThat(streetFields).hasSize(1);
        AssertionsForClassTypes.assertThat(streetFields.get(0).getId()).isEqualTo(streetField.getId());
    }
}
package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.entities.StreetField;
import at.aau.anti_mon.server.enums.GameFieldInformation;
import at.aau.anti_mon.server.enums.GameFieldType;
import at.aau.anti_mon.server.service.StreetFieldService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class StreetFieldEntityIntegrationTest {

    @Autowired
    private StreetFieldService streetFieldService;

    @Test
    @Transactional
    public void testCreateAndSaveStreetField() {
        StreetField newStreetField = new StreetField.Builder()
                .withName("TestStreetField")
                .withDescription("TestDescription")
                .withType("TestType")
                .withPosition(0)
                .withGameFieldType(GameFieldType.STREET)
                .withGameFieldPlace(GameFieldInformation.AMSTERDAM1)
                .withPrice(100)
                .withRent(20)
                .withHousePrice(50)
                .withHotelPrice(100)
                .withNumberOfHouses(0)
                .withNumberOfHotels(0)
                .build();

        // Speichere StreetField
        StreetField savedStreetField = streetFieldService.createGameField(newStreetField);

        assertNotNull(savedStreetField);
        assertEquals("TestStreetField", savedStreetField.getName());
        assertEquals("TestDescription", savedStreetField.getDescription());
        assertEquals("TestType", savedStreetField.getType());
        assertEquals(0, savedStreetField.getPosition());
        assertEquals(GameFieldType.STREET, savedStreetField.getGameFieldType());
        assertEquals(GameFieldInformation.AMSTERDAM1, savedStreetField.getGameFieldInformation());
        assertEquals(100, savedStreetField.getPrice());
        assertEquals(20, savedStreetField.getRent());
        assertEquals(50, savedStreetField.getHousePrice());
        assertEquals(100, savedStreetField.getHotelPrice());
        assertEquals(0, savedStreetField.getNumberOfHouses());
        assertEquals(0, savedStreetField.getNumberOfHotels());
    }
}
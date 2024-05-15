package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.entities.StreetField;
import at.aau.anti_mon.server.enums.GameFieldInformation;
import at.aau.anti_mon.server.enums.GameFieldType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreetFieldEntityUnitTest {

    @Test
    public void builderAssignsCorrectValues() {
        StreetField streetField = new StreetField.Builder()
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

        assertEquals("TestStreetField", streetField.getName());
        assertEquals("TestDescription", streetField.getDescription());
        assertEquals("TestType", streetField.getType());
        assertEquals(0, streetField.getPosition());
        assertEquals(GameFieldType.STREET, streetField.getGameFieldType());
        assertEquals(GameFieldInformation.AMSTERDAM1, streetField.getGameFieldInformation());
        assertEquals(100, streetField.getPrice());
        assertEquals(20, streetField.getRent());
        assertEquals(50, streetField.getHousePrice());
        assertEquals(100, streetField.getHotelPrice());
        assertEquals(0, streetField.getNumberOfHouses());
        assertEquals(0, streetField.getNumberOfHotels());
    }

    @Test
    public void builderAssignsDefaultValues() {
        StreetField streetField = new StreetField.Builder().build();

        assertEquals("TestStreetField", streetField.getName());
        assertEquals("TestDescription", streetField.getDescription());
        assertEquals("TestType", streetField.getType());
        assertEquals(0, streetField.getPosition());
        assertEquals(GameFieldType.STREET, streetField.getGameFieldType());
        assertEquals(GameFieldInformation.AMSTERDAM1, streetField.getGameFieldInformation());
        assertEquals(0, streetField.getPrice());
        assertEquals(0, streetField.getRent());
        assertEquals(0, streetField.getHousePrice());
        assertEquals(0, streetField.getHotelPrice());
        assertEquals(0, streetField.getNumberOfHouses());
        assertEquals(0, streetField.getNumberOfHotels());
    }
}
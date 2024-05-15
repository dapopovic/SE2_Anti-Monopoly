package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.entities.Game;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class GameEntityUnitTest {

    @Test
    void builderShouldSetCorrectValues() {
        Date testDate = new Date();
        Game game = new Game.Builder()
                .withName("Test Game")
                .withStartDate(testDate)
                .withRoundNumber(5)
                .build();

        assertEquals("Test Game", game.getName());
        assertEquals(testDate, game.getStartDate());
        assertEquals(5, game.getRoundNumber());
    }

    @Test
    void settersShouldSetCorrectValues() {
        Game game = new Game.Builder().build();
        Date testDate = new Date();

        game.setName("Test Game");
        game.setStartDate(testDate);
        game.setRoundNumber(5);

        assertEquals("Test Game", game.getName());
        assertEquals(testDate, game.getStartDate());
        assertEquals(5, game.getRoundNumber());
    }

    @Test
    void gettersShouldReturnCorrectValues() {
        Date testDate = new Date();
        Game game = new Game.Builder()
                .withName("Test Game")
                .withStartDate(testDate)
                .withRoundNumber(5)
                .build();

        assertEquals("Test Game", game.getName());
        assertEquals(testDate, game.getStartDate());
        assertEquals(5, game.getRoundNumber());
    }
}
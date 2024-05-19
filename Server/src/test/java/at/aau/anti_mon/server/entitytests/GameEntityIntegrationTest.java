package at.aau.anti_mon.server.entitytests;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.entities.Game;
import at.aau.anti_mon.server.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class GameEntityIntegrationTest {

    @Autowired
    private GameService gameService;

    // fixme split into multiple tests
    @Test
    @Transactional
    public void testCreateAndSaveGame() {
        Game newGame = gameService.createNewGame();

        assertNotNull(newGame);
        assertNotNull(newGame.getGameID());

        // Füge einen Spieler hinzu
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("TestPlayer");
        ResponseEntity<String> response = gameService.addPlayer(newGame.getGameID(), userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Spieler wurde erfolgreich hinzugefügt."));

        ResponseEntity<String> saveResponse = gameService.saveGame(newGame);

        assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(saveResponse.getBody()).contains("Game saved"));

        // Hole das Spiel aus der Datenbank
        Optional<Game> retrievedGame = gameService.getGames().stream()
                .filter(game -> game.getGameID().equals(newGame.getGameID()))
                .findFirst();

        // Überprüfe, ob das Spiel korrekt aus der Datenbank abgerufen wurde
        assertTrue(retrievedGame.isPresent());
        assertEquals(newGame.getGameID(), retrievedGame.get().getGameID());
    }
}

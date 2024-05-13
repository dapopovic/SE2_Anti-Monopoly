package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.dao.GameDao;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.entities.Game;
import at.aau.anti_mon.server.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    GameDao gameDAO;

    @Autowired
    GameService(GameDao gameDAO) {
        this.gameDAO = gameDAO;
    }

    /**
     * Creates a new game
     * TODO: implement
     */
    public Game createNewGame() {

        // TODO: ACHTUNG NUR TEST! Implementierung fehlt noch
        Game game = new Game.Builder().build();

        // TODO Erstelle 44 GameFields

        // Erstelle Spieler
        // TODO: Spielername sollte vom Client übergeben werden

        // Speichere das neue Spiel
        return gameDAO.save(game);
    }


    public ResponseEntity<String> saveGame(Game game) {
        gameDAO.save(game);
        return ResponseEntity.ok("Game saved");
    }

    public List<Game> getGames() {
        return gameDAO.findAll();
    }

    /**
     * Adds a player to a game in the database and returns a response entity with a message
     * @param gameID the ID of the game
     * @param userDTO the player to add
     * @return a response entity with a message
     */
    public ResponseEntity<String> addPlayer( Integer gameID, UserDTO userDTO) {
        try {
            // Spiel aus der Datenbank holen
            Optional<Game> gameOptional = gameDAO.findById(gameID);
            if (gameOptional.isEmpty()) {
                Logger.debug("Spiel nicht gefunden!");
                return ResponseEntity.badRequest().body("Spiel nicht gefunden!");
            }
            Game game = gameOptional.get();

            // Neuer Spieler erstellen
            Player newPlayer = new Player.Builder()
                    .withName(userDTO.getUsername())
                   // .withBalance(playerDTO.getBalance())
                   // .withPosition(playerDTO.getPosition())
                   // .withInJail(playerDTO.isInJail())
                    .withGame(game) // Setze das Spiel für den Spieler
                    .build();

            // Füge den Spieler zum Spieler-Set des Spiels hinzu
            game.getPlayerList().add(newPlayer);

            // Schritt 4: Persistiere Änderungen
            gameDAO.save(game);

            return ResponseEntity.ok("Spieler wurde erfolgreich hinzugefügt.");

        } catch (Exception e) {
            Logger.debug("Ein Fehler ist aufgetreten: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein Fehler ist aufgetreten: " + e.getMessage());
        }
    }


}

package at.aau.anti_mon.server.controller;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.entities.Game;
import at.aau.anti_mon.server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for the game
 * TODO: Implement
 */
@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Game> getGames() {
        return gameService.getGames();
    }

    /**
     * Creates a new game
     * TODO: Needs a "Marker" for the game, like LobbyID or Creator-UserID
     *
     * @return the created game
     */
    @PostMapping("/create")
    public ResponseEntity<Game> createNewGame() {
        Game game = gameService.createNewGame();
        return ResponseEntity.ok(game);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveGame(@RequestBody Game game) {
        return gameService.saveGame(game);
    }

    public ResponseEntity<String> addPlayer(@RequestBody Integer gameID,@RequestBody UserDTO player) {

        return gameService.addPlayer(gameID, player);
    }


}

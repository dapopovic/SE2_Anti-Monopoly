package at.aau.anti_mon.server.controller;


import at.aau.anti_mon.server.entities.PlayerEntity;
import at.aau.anti_mon.server.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {


    private final PlayerService playerService;

    @Autowired
    PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * This method maps to the /ping endpoint and returns a simple string response
     * @return ResponseEntity<String> - a simple string response
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }


    /**
     * This method maps to the root /players endpoint and returns a list of all players
     * by calling the getAllPlayer method of the playerService.
     * @return List<Player> - a list of all players
     */
    @GetMapping("")
    public List<PlayerEntity> getAllPlayer() {
        return playerService.getAllPlayer();
    }

    /**
     * This method maps to the /players/{id} endpoint and returns a player by id
     * @param id - the id of the player
     * @return Player - the player with the given id
     */
    @GetMapping("/{id}")
        public PlayerEntity getPlayerById(@PathVariable Integer id) {
        return playerService.getPlayerByID(id);
    }

    /**
     * This method maps to the /players endpoint and creates a new player
     * @param playerEntity - the player to be created
     * @return Player - the created player
     */
    @PostMapping("")
    public PlayerEntity createPlayer(@RequestBody PlayerEntity playerEntity) {
        return playerService.createPlayer(playerEntity);
    }

    /**
     * This method maps to the /players/{id} endpoint and updates a player
     * @param id - the id of the player to be updated
     * @param playerEntity - the updated player
     * @return Player - the updated player
     */
    @PutMapping("/{id}")
    public PlayerEntity updatePlayer(@PathVariable Integer id, @RequestBody PlayerEntity playerEntity) {
        return playerService.updatePlayer(id, playerEntity);
    }

    /**
     * This method maps to the /players/{id} endpoint and deletes a player
     * @param id - the id of the player to be deleted
     */
    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
    }
}


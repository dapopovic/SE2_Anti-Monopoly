package at.aau.anti_mon.server.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents the current state of the game
 */
@Entity
@Getter
@Setter
@Table(name = "GAMESTATE")
public class GameState implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gamestate_id")
    private Integer id;

    @Column(name = "gamestate_game_id")
    private Integer gameId;

    @Column(name = "gamestate_last_saved")
    private Date lastSaved;

    /**
     * Needed so the Tables can be automatically created in the DB
     * Protected since it should not be used directly
     */
    protected GameState() {
    }


}

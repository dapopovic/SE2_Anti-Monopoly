package at.aau.anti_mon.server.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Represents the current state of the game
 */
@Entity
@Getter
@Setter
@NoArgsConstructor // Needed so the Tables can be automatically created in the DB
@AllArgsConstructor
@Table(name = "GAMESTATE")
public class GameState implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private Integer gameId;

    private Integer roundNumber;


}

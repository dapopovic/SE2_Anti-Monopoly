package at.aau.anti_mon.server.entities;

import at.aau.anti_mon.server.enums.GameFieldPlace;
import at.aau.anti_mon.server.enums.GameFieldType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a field on the game board
 */
@AllArgsConstructor
@NoArgsConstructor // Needed so the Tables can be automatically created in the DB
@Getter
@Setter
@Entity
@Table(name = "GAMEFIELD")
@Inheritance(strategy = InheritanceType.JOINED)
public class GameField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gamefield_id")
    private Integer id;

    @Column(name = "gamefield_name")
    private String name;

    @Column(name = "gamefield_description")
    private String description;

    @Column(name = "gamefield_type")
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "gamefield_fieldtype")
    private GameFieldType gameFieldType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gamefield_place")
    private GameFieldPlace gameFieldPlace;

    @Column(name = "gamefield_position")
    private  int position;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

}

package at.aau.anti_mon.server.entities;

import at.aau.anti_mon.server.enums.GameFieldInformation;
import at.aau.anti_mon.server.enums.GameFieldType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a field on the game board
 */
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

    @Column(name = "gamefield_position")
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(name = "gamefield_fieldtype")
    private GameFieldType gameFieldType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gamefield_place")
    private GameFieldInformation gameFieldInformation;

    /**
     * The game the field is in
     */
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    /**
     * Needed so the Tables can be automatically created in the DB
     * Protected since it should not be used directly
     */
    protected GameField() {
    }

}

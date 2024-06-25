package at.aau.anti_mon.server.entities;


import at.aau.anti_mon.server.enums.GameStateEnum;
import at.aau.anti_mon.server.enums.Name;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Game entity
 * @author ottzoeke
 */
@Getter
@Setter
@Entity
@Table(name = "GAMES")
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Integer gameID;

    @Column(name = "game_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status")
    private GameStateEnum status;

    @Column(name = "game_round")
    private Integer roundNumber;

    /**
     * The players of the game
     */
    @OneToMany(mappedBy = "game")
    private Set<PlayerEntity> playerEntityEntityList;

    /**
     * The game fields of the game
     */
    @OneToMany(mappedBy = "game")
    private transient Set<GameField> gameFields;

    @Column(name = "game_start_date")
    private Date startDate;

    /**
     * Needed so the Tables can be automatically created in the DB
     * Protected since it should not be used directly
     */
    protected Game() {
    }

    /**
     * Builder-Constructor for the Game
     * @param builder the builder for the game
     */
    private Game(Builder builder) {
        this.name = builder.name;
        this.roundNumber = builder.roundNumber;
        this.startDate = builder.startDate;
        this.playerEntityEntityList = builder.playerEntityEntityList;
    }

    /**
     * Builder for the Game
     */
    public static class Builder {
        private String name = Name.randomName().name();  // Standardwert
        private Integer roundNumber = 0;
        private Date startDate = new Date();
        private final Set<PlayerEntity> playerEntityEntityList = new HashSet<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withRoundNumber(Integer roundNumber) {
            this.roundNumber = roundNumber;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }

}
package at.aau.anti_mon.server.entities;

import at.aau.anti_mon.server.enums.PlayerFigure;
import at.aau.anti_mon.server.enums.PlayerRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;


/**
 * Represents a player in the game
 */
@Getter
@Setter
@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

    /**
     * The ID of the player
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Integer playerID;

    /**
     * The name of the player
     */
    @NotNull
    @Size(min = 4, max = 15)
    @Column(name = "player_name", unique = true)
    private String name;

    /**
     * The balance of the players bank account
     */
    @PositiveOrZero
    @Column(name = "player_balance")
    private int balance;

    /**
     * The position of the player on the game board
     */
    @PositiveOrZero
    @Min(0)
    @Max(44)
    @Column(name = "player_position")
    private int position;

    /**
     * The player is in jail or not
     */
    @Column(name = "player_in_jail")
    private boolean inJail;

    /**
     * The game the player is in
     */
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    /**
     * The fields the player owns
     */
    @OneToMany(mappedBy = "owner")
    transient Set<StreetField> streetFieldSet;

    /**
     * The figure the player is using
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "player_figure")
    private PlayerFigure playerFigure;


    /**
     * The figure the player is using
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "player_role")
    private PlayerRole playerRole;


    /**
     * Needed so the Tables can be automatically created in the DB
     * Protected since it should not be used directly
     */
    protected Player() {
    }

    private Player(Builder builder) {
        this.name = builder.name;
        this.balance = builder.balance;
        this.position = builder.position;
        this.inJail = builder.inJail;
        this.game = builder.game;
        this.playerFigure = builder.playerFigure;
        this.playerRole = builder.playerRole;
    }

    public static class Builder {
        private String name = "TestPlayer";  // Standardwert
        private int balance = 1500;
        private int position = 0;
        private boolean inJail = false;
        private Game game = null;
        private PlayerFigure playerFigure = PlayerFigure.CAR;
        private PlayerRole playerRole = PlayerRole.ANTI_MONOPOLIST;


        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withBalance(int balance) {
            if (balance <= 0) {
                throw new IllegalArgumentException("Balance must be positive");
            }
            this.balance = balance;
            return this;
        }

        public Builder withPosition(int position) {
            if (position < 0 || position > 44) {
                throw new IllegalArgumentException("Position must be between 0 and 44");
            }
            this.position = position;
            return this;
        }

        public Builder withInJail(boolean inJail) {
            this.inJail = inJail;
            return this;
        }

        public Builder withGame(Game game) {
            this.game = game;
            return this;
        }

        public Builder withPlayerFigure(PlayerFigure playerFigure) {
            this.playerFigure = playerFigure;
            return this;
        }

        public Builder withPlayerRole(PlayerRole playerRole) {
            this.playerRole = playerRole;
            return this;
        }

        public Player build() {
            return new Player(this); // Richtig so: Erstelle ein neues Player-Objekt mit dem Builder als Parameter
        }
    }

}

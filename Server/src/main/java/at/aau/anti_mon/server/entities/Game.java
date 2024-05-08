package at.aau.anti_mon.server.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

/**
 * Game entity
 * @author ottzoeke
 */
@AllArgsConstructor
@NoArgsConstructor  // Needed so the Tables can be automatically created in the DB
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

    @Column(name = "game_status")
    private String status;

    @OneToMany(mappedBy = "game")
    private Set<Player> playerList;

   // @OneToOne(mappedBy = "playerID") // (optional = false    must specify a owner before creating a game
  //  private Player creator;

    @OneToMany(mappedBy = "game")
    private Set<GameField> gameFields;

    private Date startDate;
    private Date lastSaved;


    // Idee für Zug
    //public void nextPlayer() {
    //    currentPlayerIndex = (currentPlayerIndex + 1) % playerList.size();
    //}

    //public Player getPlayer() {
    //    return playerList.get(currentPlayerIndex);
    //}

}
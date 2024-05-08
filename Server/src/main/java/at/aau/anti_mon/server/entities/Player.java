package at.aau.anti_mon.server.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor // Needed so the Tables can be automatically created in the DB
@Getter
@Setter
@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Integer playerID;

    @Column(name = "player_name")
    private String name;

    @Column(name = "player_balance")
    private int balance;

    @Column(name = "player_position")
    private int position;

    @Column(name = "player_in_jail")
    private boolean inJail;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "owner")
    Set<StreetField> streetFieldSet;
}

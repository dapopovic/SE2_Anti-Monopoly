package at.aau.anti_mon.server.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor // Needed so the Tables can be automatically created in the DB
@Getter
@Setter
@Entity
@Table(name = "STREETFIELDS")
@PrimaryKeyJoinColumn(name = "street_gamefield_id")
public class StreetField extends GameField implements Serializable {

    @Column(name = "price")
    private int price;

    @Column(name = "rent")
    private int rent;

    @Column(name = "house_price")
    private int housePrice;

    @Column(name = "hotel_price")
    private int hotelPrice;

    @Column(name = "number_of_houses")
    private int numberOfHouses;

    @Column(name = "number_of_hotels")
    private int numberOfHotels;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Player owner;



}

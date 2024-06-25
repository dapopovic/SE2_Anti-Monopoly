package at.aau.anti_mon.server.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyGameCard {
    private int id;
    private String city;
    private String street;
    private int price;
    private int fieldNumber;

    public PropertyGameCard(int id, String city, String street, int price, int fieldNumber) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.price = price;
        this.fieldNumber = fieldNumber;
    }

}

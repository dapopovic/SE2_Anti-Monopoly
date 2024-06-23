package at.aau.anti_mon.client.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyGameCard extends GameComponent {

    String name;
    int price;
    int rent;
    int housePrice;
    int hotelPrice;
    int tax;
    String owner;
    int houses;
    int hotels;
    boolean isForSale;

    public PropertyGameCard(int id, int position) {
        super( id, position);
    }

}

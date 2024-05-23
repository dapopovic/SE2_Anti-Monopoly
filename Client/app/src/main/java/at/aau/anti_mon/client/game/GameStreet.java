package at.aau.anti_mon.client.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameStreet extends GameComponent{

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

}

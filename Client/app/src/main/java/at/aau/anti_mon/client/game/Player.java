package at.aau.anti_mon.client.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player extends GameComponent {

    GameRole role;
    User user;
    int money;


    public Player(GameRole role, User user) {
        this.role = role;
        this.user = user;
    }

    public void buyHouse(GameStreet street) {
        role.buyHouse(street);
    }

    public void sellHouse(GameStreet street) {
        role.sellHouse(street);
    }

    public void buyHotel(GameStreet street) {
        role.buyHotel(street);
    }

    public void sellHotel(GameStreet street) {
        role.sellHotel(street);
    }

    public void payRent(GameStreet street) {
        role.payRent(street);
    }

    public void payTax() {
        role.payTax();
    }

    public void drawCard() {
        role.drawCard();
    }

    public String getRole() {
        return role.getRole();
    }
}

package at.aau.anti_mon.client.game;

import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;
import lombok.Getter;

@Getter
public class Player extends GameComponent {

    GameRole gameRole;
    Roles role;
    User user;
    int money;
    Figures figure;
    PlayerInventory playerInventory;
    boolean isActive;
    String userName;


    public Player(User user) {
        super(user.hashCode(), user.playerLocation);

        this.user = user;
        this.userName = user.getUserName();
        this.role = user.getPlayerRole();
        this.figure = user.getPlayerFigure();
        this.money = DEFAULT_MONEY;

        // Initialisiere die Spielerrolle basierend auf der Benutzerrolle
        if (user.getPlayerRole().equals(Roles.COMPETITOR)) {
            this.gameRole = new AntiMonopolyst();
        } else {
            this.gameRole = new Monopolyst();
        }

        this.playerInventory = new PlayerInventory();
    }


    public void buyHouse(PropertyGameCard street) {
        gameRole.buyHouse(street);
    }

    public void sellHouse(PropertyGameCard street) {
        gameRole.sellHouse(street);
    }

    public void buyHotel(PropertyGameCard street) {
        gameRole.buyHotel(street);
    }

    public void sellHotel(PropertyGameCard street) {
        gameRole.sellHotel(street);
    }

    public void payRent(PropertyGameCard street) {
        gameRole.payRent(street);
    }

    public void payTax() {
        gameRole.payTax();
    }

    public void drawCard() {
        gameRole.drawCard();
    }

    public String getGameRole() {
        return gameRole.getRole();
    }

    public String getName() {
        return user.getUserName();
    }

    public boolean isActive() {
        return isActive;
    }

    public void move(int diceNumber) {

    }
}

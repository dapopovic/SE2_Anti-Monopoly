package at.aau.anti_mon.client.game;

public interface GameRole {

    void buyHouse(PropertyGameCard street);

    void sellHouse(PropertyGameCard street);

    void buyHotel(PropertyGameCard street);

    void sellHotel(PropertyGameCard street);

    void payRent(PropertyGameCard street);

    void payTax();

    void drawCard();

    String getRole();
}

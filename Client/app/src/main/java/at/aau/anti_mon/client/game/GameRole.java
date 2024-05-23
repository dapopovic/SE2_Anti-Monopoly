package at.aau.anti_mon.client.game;

public interface GameRole {

    void buyHouse(GameStreet street);

    void sellHouse(GameStreet street);

    void buyHotel(GameStreet street);

    void sellHotel(GameStreet street);

    void payRent(GameStreet street);

    void payTax();

    void drawCard();

    String getRole();
}

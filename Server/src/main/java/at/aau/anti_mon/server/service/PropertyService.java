package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.game.Player;
import at.aau.anti_mon.server.game.PropertyGameCard;
import at.aau.anti_mon.server.game.PropertyGameCardInitializer;

import java.util.Map;

/**
 * Service class for handling property related actions like buying, selling and paying rent.
 */
public class PropertyService {
    private final Map<Integer, PropertyGameCard> propertyGameCards;

    public PropertyService() {
        this.propertyGameCards = PropertyGameCardInitializer.initializePropertyGameCards();
    }

    public PropertyGameCard getProperty(int fieldNumber) {
        return propertyGameCards.get(fieldNumber);
    }

    public void buyProperty(int fieldNumber, Player player) {
        PropertyGameCard property = propertyGameCards.get(fieldNumber);
        if (property != null && player.getMoney() >= property.getPrice()) {
            player.decreaseMoney(property.getPrice());
            player.addProperty(property);
            System.out.println(player.getName() + " hat " + property.getStreet() + " gekauft.");
        } else {
            System.out.println("Kauf fehlgeschlagen.");
        }
    }

    public void sellProperty(int fieldNumber, Player player) {
        PropertyGameCard property = propertyGameCards.get(fieldNumber);
        if (property != null && player.ownsProperty(property)) {
            player.increaseMoney(property.getPrice());
            player.removeProperty(property);
            System.out.println(player.getName() + " hat " + property.getStreet() + " verkauft.");
        } else {
            System.out.println("Verkauf fehlgeschlagen.");
        }
    }

    public void payRent(int fieldNumber, Player player, Player owner) {
        PropertyGameCard property = propertyGameCards.get(fieldNumber);
        int rent = property.getPrice() / 10; // Beispiel für Mietpreis
        if (player.getMoney() >= rent) {
            player.decreaseMoney(rent);
            owner.increaseMoney(rent);
            System.out.println(player.getName() + " hat " + rent + " Miete für " + property.getStreet() + " an " + owner.getName() + " bezahlt.");
        } else {
            System.out.println("Nicht genug Geld, um die Miete zu bezahlen.");
        }
    }
}
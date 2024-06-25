package at.aau.anti_mon.server.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Initializer for the property game cards.
 */
public class PropertyGameCardInitializer {

    public static Map<Integer, PropertyGameCard> initializePropertyGameCards() {
        Map<Integer, PropertyGameCard> propertyGameCards = new HashMap<>();

        propertyGameCards.put(2, new PropertyGameCard(2, "Rom", "Corso Impero", 60, 2));
        propertyGameCards.put(3, new PropertyGameCard(3, "Rom", "Unter den Linden", 100, 3));
        propertyGameCards.put(6, new PropertyGameCard(6, "Flughafen", "Süden", 200, 6));
        propertyGameCards.put(7, new PropertyGameCard(7, "Berlin", "Alexanderplatz", 100, 7));
        propertyGameCards.put(9, new PropertyGameCard(9, "Berlin", "Kurfürstendamm", 100, 9));
        propertyGameCards.put(10, new PropertyGameCard(10, "Berlin", "Potsdamer Straße", 120, 10));
        propertyGameCards.put(12, new PropertyGameCard(12, "Madrid", "Plaza Mayor", 140, 12));
        propertyGameCards.put(13, new PropertyGameCard(13, "Elektrizitätswerk", "Elektrizitätswert", 150, 13));
        propertyGameCards.put(14, new PropertyGameCard(14, "Madrid", "Gran Via", 140, 14));
        propertyGameCards.put(15, new PropertyGameCard(15, "Madrid", "Paseo de la Castellana", 160, 15));
        propertyGameCards.put(16, new PropertyGameCard(16, "Straßenbahn", "Westen", 200, 16));
        propertyGameCards.put(17, new PropertyGameCard(17, "Amsterdam", "Dam", 180, 17));
        propertyGameCards.put(19, new PropertyGameCard(19, "Amsterdam", "Leidestraat", 180, 19));
        propertyGameCards.put(20, new PropertyGameCard(20, "Amsterdam", "Kalverstraat", 200, 20));
        propertyGameCards.put(22, new PropertyGameCard(22, "Paris", "Rue de la Fayette", 220, 22));
        propertyGameCards.put(24, new PropertyGameCard(24, "Paris", "Rue de la Paix", 220, 24));
        propertyGameCards.put(25, new PropertyGameCard(25, "Paris", "Champs Elysees", 240, 25));
        propertyGameCards.put(26, new PropertyGameCard(26, "Bahnhof", "Nord", 200, 26));
        propertyGameCards.put(27, new PropertyGameCard(27, "Brüssel", "Grote Markt", 260, 27));
        propertyGameCards.put(28, new PropertyGameCard(28, "Brüssel", "Hoogstraat", 260, 28));
        propertyGameCards.put(29, new PropertyGameCard(29, "Gaswerk", "Gaswerk", 150, 29));
        propertyGameCards.put(30, new PropertyGameCard(30, "Brüssel", "Nieuwstraat", 280, 30));
        propertyGameCards.put(32, new PropertyGameCard(32, "London", "Park Lane", 300, 32));
        propertyGameCards.put(33, new PropertyGameCard(33, "London", "Picadilly", 300, 33));
        propertyGameCards.put(35, new PropertyGameCard(35, "London", "Oxford Street", 320, 35));
        propertyGameCards.put(36, new PropertyGameCard(36, "Busbetriebe", "Osten", 200, 36));
        propertyGameCards.put(38, new PropertyGameCard(38, "Athen", "La Plaka", 350, 38));
        propertyGameCards.put(40, new PropertyGameCard(40, "Athen", "Syntagma", 400, 40));

        return propertyGameCards;
    }
}
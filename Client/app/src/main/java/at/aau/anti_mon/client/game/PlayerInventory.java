package at.aau.anti_mon.client.game;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class PlayerInventory {
    private final List<PropertyGameCard> propertyGameCards;
    private final List<GameCard> gameCards;

    public PlayerInventory() {
        propertyGameCards = new ArrayList<>();
        this.gameCards = new ArrayList<>();
    }

    public void addGameStreet(PropertyGameCard propertyGameCard) {
        propertyGameCards.add(propertyGameCard);
    }

    public void addGameCard(GameCard gameCard) {
        gameCards.add(gameCard);
    }

    public void removeGameStreet(PropertyGameCard propertyGameCard) {
        propertyGameCards.remove(propertyGameCard);
    }

    public void removeGameCard(GameCard gameCard) {
        gameCards.remove(gameCard);
    }
}

package at.aau.anti_mon.client.game;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class GameCard extends GameComponent {

    private final String text;
    private final int value;
    private final CardAction action;
    private static final List<GameCard> CARDS;

    public GameCard(int id, String text, int value, CardAction action) {
        super(id);
        this.text = text;
        this.value = value;
        this.action = action;
    }

    static {
        // TODO: MOOOOOOORE REAL! CARDSSSSSS
        CARDS = List.of(
                new GameCard(1, "Card 1", -30, GameCard::groupNegativeCard),
                new GameCard(2, "Card 2", -20, GameCard::negativeCard),
                new GameCard(3, "Card 3", 0, GameCard::neutralCard),
                new GameCard(4, "Card 4", 10, GameCard::positiveCard),
                new GameCard(5, "Card 5", 20, GameCard::groupPositiveCard));
    }

    public void performAction() {
        if (action != null) {
            action.performAction(this);
        }
    }

    public void shuffleCards() {
        Collections.shuffle(CARDS);
    }

    public void positiveCard(){

    }

    public void negativeCard(){

    }

    public void neutralCard(){

    }

    public void groupPositiveCard(){

    }

    public void groupNegativeCard(){

    }

    public static List<GameCard> getCards() {
        return CARDS;
    }
}
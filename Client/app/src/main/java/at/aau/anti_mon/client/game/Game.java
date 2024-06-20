package at.aau.anti_mon.client.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import lombok.Getter;

/**
 * Game-Model class
 */
@Getter
public class Game {

    private final ArrayList<GameComponent> gameStreets;
    private final Stack<GameCard> gameCards;
    private final List<Player> players;

    private int currentRound;
    private int totalRounds;

    private int currentPlayerIndex;

    GameState currentState;

    public Game(List<Player> players){
        gameStreets = new ArrayList<>(40);
        gameCards = new Stack<>();
        this.players = players;
        initializeDeck();
    }

    public boolean nextRound() {
        if (currentRound < totalRounds) {
            currentRound++;
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            return true;
        } else {
            // Spielende Logik hier
            return false;
        }
    }

    public void nextState() {
        switch (currentState) {
            case ROLL_DICE:
                currentState = GameState.MOVE_FIGURE;
                break;
            case MOVE_FIGURE:
                currentState = GameState.PLAYER_TURN;
                break;
            case PLAYER_TURN:
                currentState = GameState.NEXT_PLAYER;
                nextPlayer();
                currentState = GameState.ROLL_DICE;
                break;
        }
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) {
            currentRound++;
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean canCurrentPlayerMove() {
        return currentPlayerIndex == players.indexOf(getCurrentPlayer());
    }

    public void moveCurrentPlayer(int diceNumber) {
        Player currentPlayer = getCurrentPlayer();
        currentPlayer.move(diceNumber);
        nextRound();
    }

    private void initializeDeck() {
        List<GameCard> cards = new ArrayList<>(GameCard.getCards());
        Collections.shuffle(cards);
        gameCards.addAll(cards);
    }

    public GameCard drawCard() {
        if (!gameCards.isEmpty()) {
            return gameCards.pop();
        } else {
            System.out.println("No more cards in the deck.");
            return null;
        }
    }

}

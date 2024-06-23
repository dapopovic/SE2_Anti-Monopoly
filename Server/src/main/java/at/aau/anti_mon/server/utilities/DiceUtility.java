package at.aau.anti_mon.server.utilities;

import java.util.*;

public class DiceUtility {

    // Random-Objekt zum Würfeln
    private static final Random RANDOM = new Random();

    /**
     * Map aller Würfelergebnisse
     * K : PIN der Lobby
     * V : Lobby
     */
    private static final Map<Integer, List<Integer>> SERVER_ROLLS = new HashMap<>();

    /**
     * Rolls a die for a specific lobby
     * @return the rolled dice
     */
    public static int rollDice(Integer lobbyPIN) {
        return rollDice(lobbyPIN, 1).get(0);
    }

    /**
     * Rolls a number of dice for a specific lobby
     * @param lobbyPIN the lobby pin
     * @param number the number of dice to roll
     * @return the rolled dice
     */
    public static List<Integer> rollDice(Integer lobbyPIN, int number) {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < number; i++) {

            // Würfeln einer Zahl zwischen 1 und 6
            int roll = RANDOM.nextInt(6) + 1;
            rolls.add(roll);
        }

        SERVER_ROLLS.putIfAbsent(lobbyPIN, new ArrayList<>());
        SERVER_ROLLS.get(lobbyPIN).addAll(rolls);
        return rolls;
    }

    /**
     * Returns the rolls for a specific lobby
     * @param lobbyPIN the lobby pin
     * @return the rolls
     */
    public static List<Integer> getRolls(Integer lobbyPIN) {
        return SERVER_ROLLS.getOrDefault(lobbyPIN, new ArrayList<>());
    }

    /**
     * Returns all rolls
     * @return all rolls
     */
    public static Map<Integer, List<Integer>> getAllRolls() {
        return new HashMap<>(SERVER_ROLLS);
    }
}

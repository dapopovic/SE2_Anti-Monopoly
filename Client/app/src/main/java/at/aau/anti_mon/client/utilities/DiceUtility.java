package at.aau.anti_mon.client.utilities;

import androidx.annotation.NonNull;

import java.util.*;

public class DiceUtility {

   private static final Random RANDOM = new Random();
   private static final Map<String,List<DiceResult>> DICE_RESULTS = new HashMap<>();

    /**
     * Rolls a single die
     * @return the result of the die roll
     */
   public static int rollDie(){
       return RANDOM.nextInt(6) + 1;
   }

    /**
     * Rolls two dice and handles the logic for multiple rolls if both dice are the same.
     * @param userName the username of the player
     * @return the list of dice results from all rolls
     */
    public static List<DiceResult> rollTwoDice(String userName) {
        int counter = 0;
        List<DiceResult> results = new ArrayList<>();
        DICE_RESULTS.putIfAbsent(userName, new ArrayList<>());
        rollTwoDiceRecursive(userName, results, counter);
        return results;
    }

    private static void rollTwoDiceRecursive(String userName, List<DiceResult> results, int counter) {
        int firstDie = rollDie();
        int secondDie = rollDie();
        boolean goToJail = false;

        if (firstDie == secondDie) {
            counter++;
            if (counter >= 3) {
                goToJail = true;
            } else {
                DiceResult result = new DiceResult(firstDie, secondDie, userName, false);
                Objects.requireNonNull(DICE_RESULTS.get(userName)).add(result);
                results.add(result);
                // Rufe die Methode rekursiv auf
                rollTwoDiceRecursive(userName, results, counter);
                return;
            }
        }

        DiceResult result = new DiceResult(firstDie, secondDie, userName, goToJail);
        Objects.requireNonNull(DICE_RESULTS.get(userName)).add(result);
        results.add(result);
    }

    /**
     * Returns all dice results
     * TODO: This method can be used to show a statistic of all dice rolls at the end of the game
     * @return a map of all dice results
     */
    public static Map<String, List<DiceResult>> getAllDiceResults() {
        return new HashMap<>(DICE_RESULTS);
    }

    public record DiceResult(int firstDie, int secondDie, String userName, boolean goToJail) {

        @NonNull
        @Override
            public String toString() {
                return "DiceResult{" +
                        "userName='" + userName + '\'' +
                        ", firstDie=" + firstDie +
                        ", secondDie=" + secondDie +
                        ", goToJail=" + goToJail +
                        '}';
            }
        }
}

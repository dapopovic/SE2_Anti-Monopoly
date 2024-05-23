package at.aau.anti_mon.client.game;

public enum GameState {

    /**
     * The game is in the start phase.
     * --> Dice roll
     */
    START_TURN,

    /**
     * The player is in turn.
     * --> Player moves
     * --> Player can buy a field
     * --> Player can build on a field
     * --> Player can sell a field
     * --> Player can trade with other players
     * --> Player can end turn
     */
    PLAYER_TURN,

    THROW_DICE,

    WINNING,

    LOOSING,

    /**
     *
     */
    END_TURN;




}

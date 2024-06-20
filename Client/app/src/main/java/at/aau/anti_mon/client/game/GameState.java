package at.aau.anti_mon.client.game;

public enum GameState {

    /**
     * The game is in the initialization phase.
     */
    INITIALIZED,

    ROLL_DICE,

    MOVE_FIGURE,

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

    NEXT_PLAYER





}

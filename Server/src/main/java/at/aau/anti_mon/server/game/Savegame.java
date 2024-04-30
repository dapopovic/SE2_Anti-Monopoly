package at.aau.anti_mon.server.game;

import java.util.HashSet;

/**
 * Savegame class that holds all the information about a savegame
 * TODO: Implement this class and use database to store savegames
 */
public class Savegame {

    HashSet<User> players;

    String lobbyPin;

    String SavegameID;

}

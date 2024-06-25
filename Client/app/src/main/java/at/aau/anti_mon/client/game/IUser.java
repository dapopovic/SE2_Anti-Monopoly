package at.aau.anti_mon.client.game;

import java.util.Set;

import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;

public interface IUser {
    String getUserName();
    void setUserName(String userName);
    boolean isOwner();
    void setOwner(boolean owner);
    boolean isReady();
    void setReady(boolean isReady);
    void setPlayerMoney(int playerMoney);
    int getPlayerMoney();
    void setPlayerRole(Roles playerRole);
    Roles getPlayerRole();
    void setPlayerFigure(Figures playerFigure);
    Figures getPlayerFigure();
    boolean isCurrentPlayer();
    void setCurrentPlayer(boolean currentPlayer);
    boolean getHasLostGame();
    void setHasLostGame(boolean hasLostGame);
    int getPlayerLocation();
    void setPlayerLocation(int playerLocation);
    Set<PropertyGameCardDTO> getPropertyGameCards();
    void setPropertyGameCards(Set<PropertyGameCardDTO> propertyGameCards);
    Integer getLobbyPin();
    void setLobbyPin(Integer lobbyPin);
}
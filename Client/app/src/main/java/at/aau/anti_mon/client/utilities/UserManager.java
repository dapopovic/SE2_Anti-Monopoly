package at.aau.anti_mon.client.utilities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;
import at.aau.anti_mon.client.game.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserManager {

    private User appUser;
    private Map<String,User> userMap;
    private Integer pin;
    private static UserManager instance;


    private UserManager() {
        userMap = new java.util.HashMap<>();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void initializeUsers(List<User> users) {
        clearUsers();
        for (User user : users) {
            userMap.put(user.getUserName(), user);
        }
    }

    public void updateUsers(List<User> users) {
        for (User user : users) {
            userMap.put(user.getUserName(), user);
        }
    }

    public void putAbsentUser(User user) {
        userMap.putIfAbsent(user.getUserName(), user);
    }

    public void initializePin(Integer pin){
        this.pin = pin;
    }

    public void updateSpecificUser(User user) {
        userMap.put(user.getUserName(), user);
    }

    public Optional<User> getSpecificUser(String userName) {
        return Optional.ofNullable(userMap.get(userName));
    }

    public Collection<User> getAllUsers() {
        return userMap.values();
    }

    public ArrayList<User> getUsersAsList(){
        return new ArrayList<>(userMap.values());
    }

    public void setAppUser(User user) {
        if (user == null || user.getUserName() == null) {
            Log.e(DEBUG_TAG, "UserManager - Versuch, einen null User zu setzen / einen User ohne Namen zu setzen");
            return;
        }
        if (user.getLobbyPin() != null) {
            pin = user.getLobbyPin();
        }
        appUser = user;
        userMap.put(appUser.getUserName(), appUser);


        Log.i("UserManager", "AppUser gesetzt: " + appUser.getUserName() + " isOwner: " +
                appUser.isOwner() + " isReady: " + appUser.isReady() + " money: " +
                appUser.getPlayerMoney() + " pin: " + appUser.getLobbyPin());
    }


    public void setAppUserIsReady(boolean isReady){
        appUser.setReady(isReady);
    }

    public void setAppUserIsOwner(boolean isOwner){
        appUser.setOwner(isOwner);
    }

    public void clearUsers() {
        userMap.clear();
    }

    public void addUser(User user) {
        userMap.put(user.getUserName(), user);
    }

    public void removeUser(String user) {
        userMap.remove(user);
    }

    public void containsUser(String user) {
        userMap.containsKey(user);
    }

    public void createAppUser(String username, Integer pin) {
        this.appUser = new User.UserBuilder(username, false, false)
                .lobbyPin(pin).build();
        userMap.put(username, appUser);
    }

    public void createAppUser(String username, boolean isOwner, boolean isReady) {
        User user = new User.UserBuilder(username, isOwner, isReady)
                .build();
        userMap.put(username, user);
    }

    public void createAppUser(String username, boolean isOwner, boolean isReady, Integer pin) {
        User user = new User.UserBuilder(username, isOwner, isReady)
                .lobbyPin(pin)
                .build();
        userMap.put(username, user);
    }

    public void createAppUser(String username, boolean isOwner, boolean isReady, int money, Roles role, Figures figure) {
        User user = new User.UserBuilder(username, isOwner, isReady)
                .playerMoney(money)
                .playerRole(role)
                .playerFigure(figure)
                .build();
        userMap.put(username, user);
    }
}

 /* Use of Dagger for Singleton
 public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

   */

// private static UserManager instance;
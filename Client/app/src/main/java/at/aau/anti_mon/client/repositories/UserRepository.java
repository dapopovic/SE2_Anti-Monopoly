package at.aau.anti_mon.client.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataManager;

/**
 * Repository für Benutzer
 */
public class UserRepository {

    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<User>> getUsers() {
        return usersLiveData;
    }

    private void handleWebSocketMessage(String message) {
        // Hier verarbeitest du die Nachrichten vom WebSocket und aktualisierst die Benutzerliste
        List<User> users = Arrays.asList(JsonDataManager.parseJsonMessage(message, User[].class));
        usersLiveData.postValue(users);
    }

    public void addUser(User user) {
        List<User> currentUsers = new ArrayList<>(usersLiveData.getValue());
        currentUsers.add(user);
        usersLiveData.setValue(currentUsers);
    }

    public void updateUser(User user) {
        List<User> currentUsers = new ArrayList<>(usersLiveData.getValue());
        for (int i = 0; i < currentUsers.size(); i++) {
            if (currentUsers.get(i).getUsername().equals(user.getUsername())) {
                currentUsers.set(i, user);
                break;
            }
        }
        usersLiveData.setValue(currentUsers);
    }

    public void removeUser(User user) {
        List<User> currentUsers = new ArrayList<>(usersLiveData.getValue());
        currentUsers.removeIf(existingUser -> existingUser.getUsername().equals(user.getUsername()));
        usersLiveData.setValue(currentUsers);
    }

}

package at.aau.anti_mon.client.ui.lobby;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.data.SingleLiveEventData;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.ui.base.BaseViewModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbyViewModel extends BaseViewModel {

    private final SingleLiveEventData<String> errorLiveData = new SingleLiveEventData<>();
    private final SingleLiveEventData<String> infoLiveData = new SingleLiveEventData<>();
    private final SingleLiveEventData<Boolean> isOwner = new SingleLiveEventData<>();
    private final SingleLiveEventData<Collection<User>> startGameLiveData = new SingleLiveEventData<>();
    private final MutableLiveData<Boolean> startGameEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<String> buttonText = new MutableLiveData<>();

    private final MutableLiveData<List<User>> userListLiveData = new MutableLiveData<>(new ArrayList<>());
    private Set<User> userSet = new LinkedHashSet<>();

    private final MutableLiveData<User> appUserLiveData = new MutableLiveData<>(new User("", false, false));
    private String appUserPin;


    @Inject
    public LobbyViewModel(Application application){
        super(application);
    }

    public void onLeaveButton(LobbyActivity lobbyActivity) {
        MessagingService.createUserMessage(getAppUser().getUsername(), appUserPin, Commands.LEAVE_GAME).sendMessage();
        clearData();
        lobbyActivity.finish();
    }

    public void onToggleReadyButton() {
        MessagingService.createUserMessage(getAppUser().getUsername(), appUserPin, Commands.READY).sendMessage();
    }

    public void onStartGameButton() {
        MessagingService.createUserMessage(getAppUser().getUsername(), appUserPin, Commands.START_GAME).sendMessage();
    }

    public void onReadyEvent(User user) {
        if (user.equals(getAppUser())) {
            appUserLiveData.postValue(user);
        }
        List<User> currentList = new ArrayList<>(userSet);
        currentList.replaceAll(u -> u.getUsername().equals(user.getUsername()) ? user : u);
        userListLiveData.postValue(currentList);
        //readyUserEvent.postValue(user);
        updateButtonText();
        updateStartButton();
    }

    public void onReadyEvent(String username, boolean isReady) {
        User appUser = getAppUser();

        if (username.equals(appUser.getUsername())) {
            appUser.setReady(isReady);
            appUserLiveData.postValue(appUser);
        }
        List<User> currentList = new ArrayList<>(userSet);
        for (User user : currentList) {
            if (user.getUsername().equals(username)) {
                user.setReady(isReady);
                break;
            }
        }
        userListLiveData.postValue(currentList);
        //readyUserEvent.postValue(new User(username, false, isReady));
        updateButtonText();
        updateStartButton();
    }

    public void onStartGameEvent(User[] users) {
        startGameLiveData.postValue(List.of(users));
    }

    public void startGame(Collection<User> users) {
        startGameLiveData.postValue(users);
    }

    public void addUser(User user) {
        userSet.remove(user);
        userSet.add(user);
        userListLiveData.postValue(new ArrayList<>(userSet));
        updateStartButton();
        Log.d(DEBUG_TAG, "User joined: " + user.getUsername() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady());
    }

    public void removeUser(String username) {
        userSet.removeIf(user -> user.getUsername().equals(username));
        userListLiveData.postValue(new ArrayList<>(userSet));
        updateStartButton();
        Log.d(DEBUG_TAG, "User left: " + username);
    }

    private void updateStartButton() {
        List<User> currentList = userListLiveData.getValue();
        startGameEnabled.postValue(allReady(Objects.requireNonNull(currentList)));
    }

    public boolean allReady(List<User> currentList) {
        boolean allReady = currentList.stream().allMatch(User::isReady);
        if (currentList.size() >= 2 && getAppUser() != null && getAppUser().isOwner()) {
           return allReady;
        } else {
           return false;
        }
    }

    public void clearObservers(LifecycleOwner owner) {
        errorLiveData.removeObservers(owner);
        infoLiveData.removeObservers(owner);
        userListLiveData.removeObservers(owner);
        startGameEnabled.removeObservers(owner);
        isOwner.removeObservers(owner);
        startGameLiveData.removeObservers(owner);
    }

    public void setOwner(boolean owner) {
        isOwner.postValue(owner);
        Log.d("LobbyViewModel", "Owner set to: " + owner);
    }

    public void initAppUser(User appUser) {
        appUserLiveData.postValue(appUser);
        addUser(appUser);
        setOwner(appUser.isOwner());
        updateButtonText();
        Log.d("LobbyViewModel", "AppUser set: " + appUser.getUsername());
    }

    public void setAppUserPin(String appUserPin) {
        this.appUserPin = appUserPin;
        Log.d("LobbyViewModel", "AppUserPin set: " + appUserPin);
    }

    // Deprecated Only because of old tests
    public void onUserJoined(String username, boolean isOwner, boolean isReady) {
        addUser(new User(username, isOwner, isReady));
    }

    public void onUserLeaved(String username) {
        removeUser(username);
    }

    public void updateButtonText() {
        if (getAppUser() != null) {
            buttonText.postValue(getAppUser().isReady() ? "ready" : "not ready");
        }
    }

    public void clearData() {
        userListLiveData.postValue(new ArrayList<>());
        startGameEnabled.postValue(false);
        isOwner.postValue(false);
        startGameLiveData.postValue(null);
        appUserLiveData.postValue(null);
        appUserPin = null;
        buttonText.postValue(null);
    }

    public boolean appUserIsReady(){
        return getAppUser().isReady();
    }

    public User getAppUser() {
        return appUserLiveData.getValue();
    }

}
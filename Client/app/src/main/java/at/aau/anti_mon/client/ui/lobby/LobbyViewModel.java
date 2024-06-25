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

import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.ui.base.BaseViewModel;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.utilities.SingleLiveEvent;
import at.aau.anti_mon.client.utilities.UserManager;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LobbyViewModel extends BaseViewModel {

    private final SingleLiveEvent<String> errorLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> infoLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<Collection<User>> startGameLiveData = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> startGameEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<String> buttonText = new MutableLiveData<>();

    //User
    private final MutableLiveData<User> appUserLiveData = new MutableLiveData<>(new User.UserBuilder("", false, false).build());
    private final SingleLiveEvent<Boolean> appUserIsOwner = new SingleLiveEvent<>();

    // Command Events
    private final SingleLiveEvent<User> readyUserEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<User> userJoinedLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> userLeftLiveData = new SingleLiveEvent<>();

    private final MutableLiveData<List<User>> userListLiveData = new MutableLiveData<>(new ArrayList<>());
    private Set<User> userSet = new LinkedHashSet<>();

    UserManager userManager = UserManager.getInstance();

    @Inject
    public LobbyViewModel(Application application){
        super(application);
    }

    public void onLeaveButton(LobbyActivity lobbyActivity) {
        MessagingUtility.createUserMessage(getAppUser().getUserName(), getAppUserPin(), Commands.LEAVE_GAME).sendMessage();
        clearData();
        lobbyActivity.finish();
    }

    public void onToggleReadyButton() {
        MessagingUtility.createUserMessage(getAppUser().getUserName(), getAppUserPin(), Commands.READY).sendMessage();
    }

    public void onStartGameButton() {
        MessagingUtility.createUserMessage(getAppUser().getUserName(), getAppUserPin(), Commands.START_GAME).sendMessage();
    }

    public void onReadyEvent(User user) {
        if (user.equals(getAppUser())) {
            appUserLiveData.postValue(user);
        } else {
            readyUserEvent.postValue(user);
        }


        /*List<User> currentList = new ArrayList<>(userSet);
        currentList.replaceAll(u -> u.getUserName().equals(user.getUserName()) ? user : u);
        userListLiveData.postValue(currentList);
        //readyUserEvent.postValue(user);

         */
        updateButtonText();
        updateStartButton();

    }

    public void userJoined(String username, boolean isOwner, boolean isReady) {
        userJoinedLiveData.postValue( new User.UserBuilder(username, isOwner, isReady).build());
        //addUser(new User.UserBuilder(username, isOwner, isReady).build());
    }


    public void userLeft(String username) {
        //userLeftLiveData.postValue(username);
        removeUser(username);
        updateButtonText();
        updateStartButton();
    }

    public void onReadyEvent(String username, boolean isReady) {
        User appUser = getAppUser();
        if (username.equals(appUser.getUserName())) {
            appUser.setReady(isReady);
            appUserLiveData.postValue(appUser);
            //readyUserEvent.postValue(appUser);
        }
        List<User> currentList = new ArrayList<>(userSet);
        for (User user : currentList) {
            if (user.getUserName().equals(username)) {
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
        Log.d(DEBUG_TAG, "User joined: " + user.getUserName() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady());
    }

    public void removeUser(String username) {
        userSet.removeIf(user -> user.getUserName().equals(username));
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
        if (getAppUser() != null && getAppUser().isOwner()) {
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
        appUserIsOwner.removeObservers(owner);
        startGameLiveData.removeObservers(owner);
        readyUserEvent.removeObservers(owner);
        userJoinedLiveData.removeObservers(owner);

    }

    public void setOwner(boolean owner) {
        appUserIsOwner.postValue(owner);
        Log.d("LobbyViewModel", "Owner set to: " + owner);
    }

    public void initAppUser() {
        User appUser = userManager.getAppUser();
        if (appUser == null) {
            Log.e("LobbyViewModel", "AppUser ist null");
            return;
        }
        appUserLiveData.postValue(appUser);
        setOwner(appUser.isOwner());
        updateButtonText();
        updateStartButton();
        Log.d("LobbyViewModel", "AppUser set: " + appUser.getUserName());
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
        appUserIsOwner.postValue(false);
        startGameLiveData.postValue(null);
        readyUserEvent.postValue(null);
        buttonText.postValue(null);
    }

    public boolean appUserIsReady(){
        return getAppUser().isReady();
    }

    public User getAppUser() {
        return userManager.getAppUser();
    }

    public String getAppUserPin() {
        return String.valueOf(userManager.getPin());
    }


}


/*
@Getter
public class LobbyViewModel extends ViewModel {
    private final SingleLiveEvent<User> userJoinedLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> userLeftLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<User> readyUpLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<Collection<User>> startGameLiveData = new SingleLiveEvent<>();
    MutableLiveData<String> infoLiveData = new MutableLiveData<>();
    MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LobbyViewModel(Application application) {
        super();
    }


    // Methoden zum Aktualisieren der LiveData
    public void userJoined(String username, boolean isOwner, boolean isReady) {
        userJoinedLiveData.postValue( new User.UserBuilder(username, isOwner, isReady).build());
    }

    public void userLeft(String username) {
        userLeftLiveData.postValue(username);
    }

    public void readyUp(String username, boolean isReady) {
        Log.d("LobbyViewModel", "User is ready " + username);
        readyUpLiveData.postValue(new User.UserBuilder(username, false, isReady).build());

    }
    public void startGame(Collection<User> users) {
        startGameLiveData.postValue(users);
    }
}

 */
package at.aau.anti_mon.client.ui.gameboard;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.collection.SparseArrayCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.events.ChangeBalanceEvent;
import at.aau.anti_mon.client.events.CheatingEvent;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.events.LooseGameEvent;
import at.aau.anti_mon.client.events.NextPlayerEvent;
import at.aau.anti_mon.client.game.Game;
import at.aau.anti_mon.client.game.GameState;
import at.aau.anti_mon.client.game.PropertyGameCard;
import at.aau.anti_mon.client.game.PropertyGameCardInitializer;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.utilities.DiceUtility;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.ui.base.BaseViewModel;
import at.aau.anti_mon.client.utilities.SingleLiveEvent;
import lombok.Getter;
import lombok.Setter;


/**
 * Model-View-ViewModel for the game field.
 * Provides a clear separation of concerns between the UI and the game logic.
 * This ViewModel is responsible for managing the game state, game logic and users.
 */
@Setter
@Getter
public class GameBoardViewModel extends BaseViewModel {

    // Game
    private final MutableLiveData<Game> gameLiveData = new MutableLiveData<>();
    private final MutableLiveData<GameState> gameState = new MutableLiveData<>(GameState.INITIALIZED);
    private final SparseArrayCompat<PropertyGameCard> propertyGameCardMap;

    // User
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> appUsersTurn = new MutableLiveData<>(false);

    // Dice:
    private final MutableLiveData<DiceUtility.DiceResult> diceResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> doubleDiceLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> enableFinishButtonLiveData = new MutableLiveData<>(false);

    //CommandEvents
    private final SingleLiveEvent<NextPlayerEvent> nextPlayerLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<ChangeBalanceEvent> balanceChangeEventData = new SingleLiveEvent<>();
    private final SingleLiveEvent<LooseGameEvent> looseGameEventData = new SingleLiveEvent<>();
    private final SingleLiveEvent<DiceNumberReceivedEvent> diceNumberData = new SingleLiveEvent<>();
    private final SingleLiveEvent<CheatingEvent> cheatingEventData = new SingleLiveEvent<>();

    //Events
    private final SingleLiveEvent<Void> launchDiceActivityEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> launchSettingsActivityEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> launchHandelActivityEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> launchObjectsActivityEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> launchRoleActivityEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> finishRoundEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<View> launchImageViewEvent = new SingleLiveEvent<>();


    private final SingleLiveEvent<String> errorLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> infoLiveData = new SingleLiveEvent<>();



    private final MutableLiveData<List<User>> users = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showDialog = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> surrender = new MutableLiveData<>(false);
    private final MutableLiveData<String> pin = new MutableLiveData<>();

    @Inject GlobalEventQueue queue;

    private User appUser;
    private Integer lobbyPin;

    @Inject
    public GameBoardViewModel(Application application) {
        super(application);
        propertyGameCardMap = PropertyGameCardInitializer.initializePropertyGameCards();
    }



    public MutableLiveData<View> getLaunchImageViewEvent() {
        return launchImageViewEvent;
    }



    public void sendDice(int dice1, int dice2, boolean cheat) {
        int dicenumber = dice1 + dice2;
        MessagingUtility.createDiceNumberMessage(appUser.getUserName(), dicenumber,lobbyPin,cheat).sendMessage();
        Log.println(Log.DEBUG, "ActivityGameField", "Send dicenumber to server.");
    }

    public void onChangeBalanceEvent(){
        MessagingUtility.createChangeBalanceMessage(getCurrentUserFromLiveData().getUserName(), -1 ).sendMessage();
    }

    public void onLooseGameEvent(){
        MessagingUtility.createUserMessage(getCurrentUserFromLiveData().getUserName(), Commands.LOSE_GAME).sendMessage();
    }

    public User getCurrentUserFromLiveData() {
        return currentUser.getValue();
    }

    public void setCurrentUser(User appUser) {
        this.currentUser.postValue(appUser);
    }

    public void setDoubleDice(Boolean value) {
        this.doubleDiceLiveData.postValue(value);
    }

    public LiveData<Void> getLaunchDiceActivityEvent() {
        return launchDiceActivityEvent;
    }

    public void onFinishRound(){
        Log.d(DEBUG_TAG, "Player ends turn - Send name:" + appUser.getUserName());
        Log.d(DEBUG_TAG, "Player Money:" + appUser.getPlayerMoney());
        if(appUser.getPlayerMoney()<0){
            onLooseGameEvent();
        }
        appUsersTurn.postValue(false);
        MessagingUtility.createUserMessage(appUser.getUserName(), Commands.NEXT_PLAYER).sendMessage();
        finishRoundEvent.trigger();
    }

    public void onFigureMove() {
        launchDiceActivityEvent.trigger();
    }

    public void onRoleClick() {
        launchRoleActivityEvent.trigger();
    }

    public void onObjectsClick() {
        launchObjectsActivityEvent.trigger();
    }

    public void onSettingsClick() {
        launchSettingsActivityEvent.trigger();
    }

    public void onHandelClick() {
        launchHandelActivityEvent.trigger();
    }

    public void onImageViewClick(View view) {
        launchImageViewEvent.setValue(view);
    }

    public LiveData<Void> getLaunchSettingsActivityEvent() {
        return launchSettingsActivityEvent;
    }

    public LiveData<Void> getLaunchHandelActivityEvent() {
        return launchHandelActivityEvent;
    }

    public LiveData<Void> getLaunchObjectsActivityEvent() {
        return launchObjectsActivityEvent;
    }

    public LiveData<Void> getLaunchRoleActivityEvent() {
        return launchRoleActivityEvent;
    }

    public LiveData<Void> getFinishRoundEvent() {
        return finishRoundEvent;
    }

    public PropertyGameCard getPropertyGameCard(int viewId) {
        return propertyGameCardMap.get(viewId);
    }

    public void onFieldClick(int fieldPosition) {
        // Logik für den Klick auf ein Spielfeld
        Log.d("GameBoardViewModel", "Field " + fieldPosition + " clicked");
    }

    public void setAppUsersTurn(boolean b) {
        this.appUsersTurn.postValue(b);
    }


    public void nextPlayerEvent(NextPlayerEvent event) {
        nextPlayerLiveData.postValue(event);
    }

    public void balanceChangeEvent(ChangeBalanceEvent changeBalanceEvent) {
        balanceChangeEventData.postValue(changeBalanceEvent);
    }

    public void looseGameEvent(LooseGameEvent looseGameEvent) {
        looseGameEventData.postValue(looseGameEvent);
    }

    public void diceNumberReceivedEvent(DiceNumberReceivedEvent diceNumberReceivedEvent) {
        diceNumberData.postValue(diceNumberReceivedEvent);
    }

    public void cheatingEvent(CheatingEvent cheatingEvent) {
        cheatingEventData.postValue(cheatingEvent);
    }


}

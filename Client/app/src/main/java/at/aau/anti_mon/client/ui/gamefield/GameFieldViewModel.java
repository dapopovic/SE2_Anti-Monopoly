package at.aau.anti_mon.client.ui.gamefield;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.collection.SparseArrayCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.game.Game;
import at.aau.anti_mon.client.game.GameState;
import at.aau.anti_mon.client.game.Player;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.manager.ResourceManager;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.data.SingleLiveEventData;
import at.aau.anti_mon.client.ui.base.BaseViewModel;
import lombok.Getter;
import lombok.Setter;


/**
 * Model-View-ViewModel for the game field.
 * Provides a clear separation of concerns between the UI and the game logic.
 * This ViewModel is responsible for managing the game state, game logic and users.
 */
@Getter
@Setter
public class GameFieldViewModel extends BaseViewModel {

    private static final int MAX_FIELD_COUNT = 40;

    // Game-Model
    private MutableLiveData<Game> gameLiveData;
    private MutableLiveData<Player> currentPlayerLiveData;

    private MutableLiveData<GameState> gameState = new MutableLiveData<>(GameState.INITIALIZED);

    private MutableLiveData<Player> currentPlayer = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<List<User>> userTurnData = new MutableLiveData<>(new ArrayList<>());

    private MutableLiveData<Integer> diceRoll = new MutableLiveData<>();
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();

    private SingleLiveEventData<String> showDialogEvent = new SingleLiveEventData<>();
    private final SingleLiveEventData<DiceNumberReceivedEvent> diceNumberData = new SingleLiveEventData<>();

    // SparseArrayCompat für das Mapping von View-IDs zu Bild- und Textressourcen
    private SparseArrayCompat<int[]> resourceMap;
    private SparseArrayCompat<Integer> fieldPositions;
    // Positionen der Spielfiguren
    private MutableLiveData<int[]> figurePositions = new MutableLiveData<>(new int[40]);
    private SparseArrayCompat<View> fieldViews = new SparseArrayCompat<>();

    private Random random = new Random();

    private ArrayList<User> users;

    private String appUserPin;
    private User appUser;
    private int currentPlayerIndex = 0;

    private ResourceManager resourceManager = new ResourceManager();

    @Inject
    public GameFieldViewModel(Application application){
        super(application);
        gameLiveData = new MutableLiveData<>();
        currentPlayerLiveData = new MutableLiveData<>();
    }

    public void processIntent(Intent intent) {
        if (!intent.hasExtra("users") || !intent.hasExtra("currentUser") || !intent.hasExtra("pin")) {
            Log.e(DEBUG_TAG, "Intent is missing extras");
            return;
        }

        User[] usersList = JsonDataManager.parseJsonMessage(intent.getStringExtra("users"), User[].class);
        List<User> users = new ArrayList<>();
        Collections.addAll(users, usersList);
        setUserTurnData(users);


        // Erstelle Player-Objekte aus den User-Objekten
        List<Player> players = new ArrayList<>();
        for (User user : users) {
            Player player = new Player(user);
            Log.d(DEBUG_TAG, "Player: " + player.getName() + " money: " + player.getMoney() + " location: " + player.getPosition() + " isActive: " + player.isActive() + " role: " + player.getRole() + " figure: " + player.getFigure());
            players.add(player);
        }

        appUser = JsonDataManager.parseJsonMessage(intent.getStringExtra("currentUser"), User.class);
        if (appUser != null) {
            Log.d(DEBUG_TAG, "Current User: " + appUser.getUsername() + " isOwner: " + appUser.isOwner() + " isReady: " + appUser.isReady() + " money: " + appUser.getMoney());
        }

        setAppUserPin(intent.getStringExtra("pin"));

        // TODO: Von Alexanderr
        //gameFieldViewModel.notifyCurrentPlayer();
        // show the current role of the user in a popup
        //Intent i = new Intent(getApplicationContext(), PopActivityRole.class);
        //i.putExtra("role", currentUser.getRole().name());
        //i.putExtra("username", currentUser.getUsername());
        //i.putExtra("figure", currentUser.getFigure().name());
        //startActivity(i);
    }

    public void leaveGame() {
        MessagingService.createUserMessage(appUser.getUsername(),appUserPin, Commands.LEAVE_GAME).sendMessage();
    }

    public void rollDice(String username, Integer dicenumber, String figure, Integer location) {
        Log.d("LobbyViewModel", "User is ready " + username);
        diceNumberData.postValue(new DiceNumberReceivedEvent(dicenumber, username, figure, location));
    }

    public void rollLocalDice() {
        int diceNumber = new Random().nextInt(6) + 1; // Würfeln
        diceRoll.setValue(diceNumber);
       // gameController.handleDiceRoll(diceNumber);
    }

    public void notifyCurrentPlayer() {
        //Player currentPlayer = gameController.getCurrentPlayer();
        //this.currentPlayer.setValue(currentPlayer);
        //showDialogEvent.setValue("Player " + currentPlayer.getName() + ", it's your turn!");
    }


    public void setUserTurnData(List<User> userTurnData) {
        this.usersLiveData.setValue(userTurnData);
    }

    public void setCurrentUser(User user) {
        this.currentUser.setValue(user);
    }

    public SparseArrayCompat<int[]> getResourceMap() {
        return resourceManager.getResourceMap();
    }

    public SparseArrayCompat<Integer> getFieldPositions() {
        return resourceManager.getFieldPositions();
    }

    public int rollDice() {
        return random.nextInt(11) + 2;
    }

    private void updateBalanceForLapCompletion(String username, boolean isEndOfTurn) {
        if (username.equals(currentUser.getValue().getUsername())) {
            int new_balance = currentUser.getValue().getMoney() + (isEndOfTurn ? 200 : 100);
            MessagingService.createGameBalanceMessage(currentUser.getValue().getUsername(), new_balance, Commands.CHANGE_BALANCE).sendMessage();
        }
    }



    public void setUpResourceMap(){
        resourceManager = new ResourceManager();
        resourceMap = resourceManager.getResourceMap();
        fieldPositions = resourceManager.getFieldPositions();
    }


    public void onImageViewClick(View view) {
        int viewId = view.getId();
        int[] resources = getResourceMap().get(viewId);
        if (resources != null) {
            showCustomDialog(view.getContext(), resources[0], view.getContext().getString(resources[1]));
        }
    }

    private void showCustomDialog(Context context, int imageResId, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom_linearlayout, null);

        ImageView imageView = view.findViewById(R.id.dialog_image);
        imageView.setImageResource(imageResId);

        TextView textView = view.findViewById(R.id.dialog_description);
        textView.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Feldinformation")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }



    public void setFieldView(int position, View view) {
        fieldViews.put(position, view);
    }

    public View getFieldView(int position) {
        return fieldViews.get(position);
    }

    public void moveFigure(Player player, int diceNumber) {
        int currentPosition = player.getPosition();
        int newPosition = (currentPosition + diceNumber) % MAX_FIELD_COUNT;
        player.setPosition(newPosition);
        updateFigurePosition(player);
    }

    private void updateFigurePosition(Player player) {
        View newPositionView = getFieldView(player.getPosition());
        if (newPositionView != null) {
            // Aktualisiere die Position der Spielfigur
            //player.getFigure().set(newPositionView.getX());
            //player.getFigure().setY(newPositionView.getY());
        }
    }

    void moveFigure(String username, int location, int diceNumber, ImageView figure, Context context) {
        for (int i = 1; i <= diceNumber; i++) {
            if (location == MAX_FIELD_COUNT) {
                location = 0;
                updateBalanceForLapCompletion(username, i == diceNumber);
            }
            location++;
            //updateFigurePosition(location, figure, context );
        }
    }

    public void updateFigurePosition(int figureIndex, int newPosition) {
        int[] positions = figurePositions.getValue();
        if (positions != null && figureIndex >= 0 && figureIndex < positions.length) {
            positions[figureIndex] = newPosition;
            figurePositions.setValue(positions);
        }
    }

    private int updateLocation(int currentLocation, int diceNumber) {
        return (currentLocation + diceNumber > MAX_FIELD_COUNT) ? 1 : currentLocation + diceNumber;
    }



    public void onEndGame(View view) {
        MessagingService.createUserMessage(appUser.getUsername(), appUserPin, Commands.LEAVE_GAME).sendMessage();
    }

    public void onFigureMove(View view) {
        //int randomNumber = random.nextInt(11) + 2;
        int randomNumber = 7;
        MessagingService.createGameMessage(appUser.getUsername(), randomNumber, Commands.RANDOM_DICE).sendMessage();
        Log.d("ActivityGameField", "Send dicenumber to server.");
    }


    private void logUsersInfo() {
        users.forEach(user -> Log.d(DEBUG_TAG, "User: " + user.getUsername() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady() + " money: " + user.getMoney()));
    }


    public void nextTurn() {
        List<User> userList = userTurnData.getValue();
        if (userList != null && !userList.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % userList.size();
            currentUser.setValue(userList.get(currentPlayerIndex));
        }
    }

    public void addUser(User user) {
        List<User> userList = userTurnData.getValue();
        if (userList != null) {
            userList.add(user);
            userTurnData.setValue(userList);
        }
    }

    public void startGame() {
        List<User> userList = userTurnData.getValue();
        if (userList != null && !userList.isEmpty()) {
            currentPlayerIndex = 0;
            currentUser.setValue(userList.get(currentPlayerIndex));
        }
    }

    public void addPlayer(User user) {
        List<User> userList = userTurnData.getValue();
        if (userList != null) {
            userList.add(user);
            userTurnData.setValue(userList);
        }
    }


    /////////////////////////////////////////// GAME MODEL:

    public void startNewGame(List<Player> players) {
        Game game = new Game(players);
        gameLiveData.setValue(game);
        currentPlayerLiveData.setValue(game.getCurrentPlayer());
    }

    public void moveCurrentPlayer(int diceNumber) {
        Game game = gameLiveData.getValue();
        if (game != null && game.getCurrentState() == GameState.MOVE_FIGURE) {
            game.getCurrentPlayer().move(diceNumber); // Implementiere die Logik zur Bewegung des Spielers
            advanceGameState();
        }
    }

    public void advanceGameState() {
        Game game = gameLiveData.getValue();
        if (game != null) {
            game.nextState();
            gameLiveData.setValue(game);
            currentPlayerLiveData.setValue(game.getCurrentPlayer());
            gameState.setValue(game.getCurrentState());
        }
    }

    public void performAction() {
        //  Logik für die Aufgaben des aktuellen Spielers
        advanceGameState();
    }



    public void clearObservers(LifecycleOwner owner) {

    }


    public void setFirstPlayer(String username) {
    }
}

package at.aau.anti_mon.client.activities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.multidex.MultiDex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

/**
 * LobbyActivity class to handle the lobby of the game
 */
public class LobbyActivity extends AppCompatActivity {


    //////////////////////////////////// Android UI
    TextView textViewPin;

    Button startButton;
    Button readyButton;


    HashMap<LinearLayout, User> availableUsers = new HashMap<>();




    ///////////////////////////////////// Variablen
    private String pin;
    private User user;
    private boolean leftLobby = false;
    private boolean gameStarted = false;

    ///////////////////////////////////// Networking

    /**
     * Dependency Injection of WebSocketClient
     */
    @Inject
    WebSocketClient webSocketClient;

    /**
     * Dependency Injection of GlobalEventQueue
     */
    @Inject
    GlobalEventQueue globalEventQueue;

    SharedPreferences sharedPreferences;

    @Inject
    LobbyViewModel lobbyViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);
        // TODO:
        // SharedPreferences für zu speichernde Key-Value Paare
        // sharedPreferences = getSharedPreferences(username, MODE_PRIVATE);

        // Setup der UI und andere Initialisierungen
        initializeUI();
        // Nachdem die UI initialisiert wurde und der EventBus registriert ist, Netzwerkdienste starten
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);

        setupLiveDataObservers();
    }

    private void initializeUI() {
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        LinearLayout[] userLayoutViews = new LinearLayout[]{
                findViewById(R.id.user1),
                findViewById(R.id.user2),
                findViewById(R.id.user3),
                findViewById(R.id.user4),
                findViewById(R.id.user5),
                findViewById(R.id.user6),
        };

        // Alle TextViews als verfügbar markieren:
        for (LinearLayout lt : userLayoutViews) {
            availableUsers.put(lt, null);
        }

        textViewPin = findViewById(R.id.Pin);
        startButton = findViewById(R.id.lobby_start_game);
        readyButton = findViewById(R.id.lobby_ready);


        processIntent();
    }

    private void processIntent() {
        if (getIntent().hasExtra("username")) {
            user = new User(getIntent().getStringExtra("username"), getIntent().getBooleanExtra("isOwner", false), getIntent().getBooleanExtra("isReady", false));
            addUserToTable(user);
        } else {
            Log.e(DEBUG_TAG, "Old Intent has no username");
            // Todo: Error handling
        }
        if (getIntent().hasExtra("pin")) {
            pin = getIntent().getStringExtra("pin");
            textViewPin.setText(pin);
        } else {
            Log.e(DEBUG_TAG, "Old Intent has no pin");
            // Todo: Error handling
        }
    }

    private void addUserToTable(User user) {
        if (this.user.equals(user) && !user.isOwner()) {
            // start game button is only visible for the owner
            findViewById(R.id.lobby_start_game).setVisibility(View.GONE);
            findViewById(R.id.lobby_ready).setVisibility(View.VISIBLE);
        }
        Log.d(DEBUG_TAG, "User joined: " + user.getUsername() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady());
        for (Map.Entry<LinearLayout, User> entry : availableUsers.entrySet()) {
            if (entry.getValue() == null) {  // Prüfe, ob der TextView verfügbar ist
                // get the first available user layout
                TextView tv = (TextView) entry.getKey().getChildAt(0);
                tv.setText(user.getUsername());
                CheckBox cb = (CheckBox) entry.getKey().getChildAt(1);
                if (user.isOwner()) {
                    tv.setTextColor(Color.RED);
                }
                cb.setChecked(user.isReady());
                availableUsers.put(entry.getKey(), user);  // Markiere als besetzt
                int size = availableUsers.entrySet().stream().filter(e -> e.getValue() != null).toArray().length;
                Log.d(DEBUG_TAG, "Size of available users: " + size);
                if (size >= 2 && this.user.isOwner()) {
                    Button startButton = findViewById(R.id.lobby_start_game);
                    startButton.setEnabled(false);
                    startButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.rounded_btn_disabled));
                }
                return;
            }
        }
        Log.e(DEBUG_TAG, "Kein verfügbarer Platz für neuen Benutzer.");
    }

    private void removeUserFromTable(String username) {
        // get size of availableUsers
        for (Map.Entry<LinearLayout, User> entry : availableUsers.entrySet()) {
            if (entry.getValue() != null && entry.getValue().getUsername().equals(username)) {
                TextView tv = (TextView) entry.getKey().getChildAt(0);
                tv.setText("");
                CheckBox cb = (CheckBox) entry.getKey().getChildAt(1);
                cb.setChecked(false);
                availableUsers.put(entry.getKey(), null);  // Markiere als verfügbar
                int size = availableUsers.entrySet().stream().filter(e -> e.getValue() != null).toArray().length;
                if (size == 2 && user.isOwner()) {
                    Button startButton = findViewById(R.id.lobby_start_game);
                    startButton.setEnabled(false);
                    // make it a little pale
                    startButton.setBackgroundColor(Color.parseColor("#FFD3D3D3"));
                }
                return;
            }
        }
        Log.e(DEBUG_TAG, "Benutzername nicht gefunden.");
    }

    /**
     * Heartbeat to keep connection alive
     *
     * @param event HeartBeatEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {
        Log.d(DEBUG_TAG, "HeartBeatEvent");

        JsonDataDTO jsonData = new JsonDataDTO(Commands.HEARTBEAT, new HashMap<>());
        jsonData.putData("msg", "PONG");
        String jsonMessage = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonMessage);
    }

    public void onReadyEvent(User eventUser) {
        AtomicBoolean allReady = new AtomicBoolean(true);
        availableUsers.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .forEach(entry -> updateReadyStatusForUser(entry, eventUser, allReady));

        updateStartButton(allReady.get());
    }

    private void updateReadyStatusForUser(Map.Entry<LinearLayout, User> entry, User eventUser, AtomicBoolean allReady) {
        User currentUser = entry.getValue();
        if (isCurrentUser(currentUser, eventUser)) {
            currentUser.setReady(eventUser.isReady());
            updateCheckBox(entry, eventUser.isReady());
            updateReadyButton(eventUser.isReady());
            if (!currentUser.isReady()) {
                allReady.set(false);
            }
        }
    }

    private void updateCheckBox(Map.Entry<LinearLayout, User> entry, boolean isReady) {
        CheckBox cb = (CheckBox) entry.getKey().getChildAt(1);
        cb.setChecked(isReady);
    }

    private boolean isCurrentUser(User eventUser, User currentUser) {
        return currentUser.getUsername().equals(eventUser.getUsername());
    }

    private void updateReadyButton(boolean isReady) {
        readyButton.setText(isReady ? R.string.unready : R.string.ready);
    }

    private void updateStartButton(boolean allReady) {
        //boolean allReady = areAllUsersReady();
        startButton.setEnabled(user.isOwner() && allReady);
        startButton.setBackground(AppCompatResources.getDrawable(this, allReady ? R.drawable.rounded_btn : R.drawable.rounded_btn_disabled));
    }

    private boolean areAllUsersReady() {
        return availableUsers.values().stream().allMatch(User::isReady);
    }



    /**
     * TEST LiveData and Observer Pattern
     */
    private void setupLiveDataObservers() {

        // User joined: -> LiveData
        lobbyViewModel.getUserJoinedLiveData().observe(this, this::addUserToTable);

        // User left: -> LiveData
        lobbyViewModel.getUserLeftLiveData().observe(this, this::removeUserFromTable);

        // User ready: -> LiveData
        lobbyViewModel.getReadyUpLiveData().observe(this, this::onReadyEvent);

        // user started game -> LiveData
        lobbyViewModel.getStartGameLiveData().observe(this, this::startGame);
    }

    private void removeObservers() {
        lobbyViewModel.getUserJoinedLiveData().removeObservers(this);
        lobbyViewModel.getUserLeftLiveData().removeObservers(this);
        lobbyViewModel.getReadyUpLiveData().removeObservers(this);
        lobbyViewModel.getStartGameLiveData().removeObservers(this);
    }

    private void startGame(Collection<User> users) {
        Intent intent = new Intent(this, ActivityGameField.class);
        intent.putExtra("users", JsonDataManager.createJsonMessage(users));
        intent.putExtra("currentUser", JsonDataManager.createJsonMessage(user));
        intent.putExtra("pin", pin);
        gameStarted = true;

        // TODO: Initialize Database

        startActivity(intent);
    }

    public void onCancelLobby(View view) {
        // leave lobby
        leaveLobby();
        // Go back to last Activity on Stack (JoinGameActivity)
        finish();
    }

    private void leaveLobby() {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.LEAVE_GAME, new HashMap<>());
        jsonData.putData("username", user.getUsername());
        jsonData.putData("pin", pin);
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        leftLobby = true;

        Log.d(DEBUG_TAG, " Username sending to leave Lobby:" + jsonDataString);
    }

    public void onStartGame(View view) {
        // send start game message to server
        JsonDataDTO jsonData = new JsonDataDTO(Commands.START_GAME, new HashMap<>());
        jsonData.putData("username", user.getUsername());
        jsonData.putData("pin", pin);
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.d(DEBUG_TAG, " Username sending to start game:" + jsonDataString);
    }

    public void onReady(View view) {
        // send ready message to server
        JsonDataDTO jsonData = new JsonDataDTO(Commands.READY, new HashMap<>());
        jsonData.putData("username", user.getUsername());
        jsonData.putData("pin", pin);
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.d(DEBUG_TAG, " Username sending to ready up:" + jsonDataString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameStarted) {
            finish();
        }
        EventBus.getDefault().register(this);
        Log.d(DEBUG_TAG, "EventBus registered");
        globalEventQueue.setEventBusReady(true);
        if (webSocketClient != null) {
            webSocketClient.setUserId(user.getUsername());
            webSocketClient.connectToServer();
        }
        if (!lobbyViewModel.getUserJoinedLiveData().hasActiveObservers()) {
            setupLiveDataObservers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObservers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!leftLobby && !gameStarted) {
            leaveLobby();
        }
        if (webSocketClient != null && !gameStarted) {
            webSocketClient.disconnect();
        }
        EventBus.getDefault().unregister(this);
        Log.d(DEBUG_TAG, "EventBus unregistered");
        globalEventQueue.setEventBusReady(false);
        removeObservers();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}

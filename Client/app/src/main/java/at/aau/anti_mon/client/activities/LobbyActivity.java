package at.aau.anti_mon.client.activities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.adapters.LobbyUserAdapter;
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
    //HashMap<LinearLayout, User> availableUsers = new HashMap<>();

    private RecyclerView recyclerView;
    private LobbyUserAdapter userAdapter;
    ArrayList<User> userList = new ArrayList<>(); // to initialize the list as empty

    ///////////////////////////////////// Variablen
    private String pin;
    private User user;
    private boolean leftLobby = false;
    private boolean gameStarted = false;

    ///////////////////////////////////// Networking

    @Inject WebSocketClient webSocketClient;
    @Inject GlobalEventQueue globalEventQueue;
    @Inject LobbyViewModel lobbyViewModel;

    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        injectDependencies();
        initializeUI();

        userAdapter = new LobbyUserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
        processIntentAfterCreate();
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

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // initialize UI views
        textViewPin = findViewById(R.id.Pin);
        startButton = findViewById(R.id.lobby_start_game);
        readyButton = findViewById(R.id.lobby_ready);

    }

    private void processIntentAfterCreate() {
        if (getIntent().hasExtra("username")) {
            user = new User(getIntent().getStringExtra("username"),
                    getIntent().getBooleanExtra("isOwner", false),
                    getIntent().getBooleanExtra("isReady", false)
            );
            addUserToTable(user);
        } else {
            Log.e(DEBUG_TAG, "Old Intent has no username");
            Toast.makeText(this, "Username missing", Toast.LENGTH_SHORT).show();
            finish();
            //TODO Error Handling
        }
        if (getIntent().hasExtra("pin")) {
            pin = getIntent().getStringExtra("pin");
            textViewPin.setText(pin);
        } else {
            Log.e(DEBUG_TAG, "Old Intent has no pin");
            Toast.makeText(this, "PIN missing", Toast.LENGTH_SHORT).show();
            //finish();
            //TODO Error Handling
        }
    }

    /**
     * Adds a user to the table after the LiveData has been updated
     * @see LobbyViewModel
     * @param newUser The user that joined the lobby
     */
    private void addUserToTable(User newUser) {
        if (isCurrentUser(newUser, user) && !newUser.isOwner()) {
            startButton.setVisibility(View.GONE);
            readyButton.setVisibility(View.VISIBLE);
        }
        Log.d(DEBUG_TAG, "User joined: " + newUser.getUsername() + " isOwner: " + newUser.isOwner() + " isReady: " + newUser.isReady());

        userAdapter.addUser(newUser);
        updateStartButton(userAdapter.areAllUsersReady());
    }

    /**
     * Removes a user from the table after the LiveData has been updated
     * @see LobbyViewModel
     * @param username The username of the user that left the lobby
     */
    private void removeUserFromTable(String username) {
        userAdapter.removeUser(username);
        updateStartButton(userAdapter.areAllUsersReady());
    }

    private void updateStartButton(boolean allReady) {
        int size = userAdapter.getItemCount();
        Log.d(DEBUG_TAG, "Size of available users: " + size);
        if (size >= 2 && user.isOwner()) {
            startButton.setEnabled(allReady);
            startButton.setBackground(AppCompatResources.getDrawable(this, allReady ? R.drawable.rounded_btn : R.drawable.rounded_btn_disabled));
        }
    }

    private boolean isCurrentUser(User eventUser, User currentUser) {
        return currentUser.getUsername().equals(eventUser.getUsername());
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

    public void onReadyEvent(User eventUser) {
        userAdapter.setReady(eventUser.getUsername(), eventUser.isReady());
        readyButton.setText(user.isReady() ? R.string.ready : R.string.not_ready);
        updateStartButton(userAdapter.areAllUsersReady());
    }

    /**
     * On ready button click send message to server
     */
    public void onReady(View view) {
        String jsonDataString = prepareJsonDataDTO(Commands.READY);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.d(DEBUG_TAG, " Username sending to ready up:" + jsonDataString);
    }

    /**
     * On start game button click send message to server
     */
    public void onStartGame(View view) {
        String jsonDataString = prepareJsonDataDTO(Commands.START_GAME);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.d(DEBUG_TAG, " Username sending to start game:" + jsonDataString);
    }

    public void onCancelLobby(View view) {
        // leave lobby
        leaveLobby();
        // Go back to last Activity on Stack (JoinGameActivity)
        finish();
    }

    private void leaveLobby() {
        String jsonDataString = prepareJsonDataDTO(Commands.LEAVE_GAME);
        webSocketClient.sendMessageToServer(jsonDataString);
        leftLobby = true;
        Log.d(DEBUG_TAG, " Username sending to leave Lobby:" + jsonDataString);
    }


    /**
     * Prepares the JsonDataDTO for the given command
     * @param command The command to prepare the JsonDataDTO for (e.g. START_GAME, READY)
     * @return The JsonDataDTO as a String
     */
    public String prepareJsonDataDTO(Commands command) {
        JsonDataDTO jsonData = new JsonDataDTO(command, new HashMap<>());
        jsonData.putData("username", user.getUsername());
        jsonData.putData("pin", pin);
        return JsonDataManager.createJsonMessage(jsonData);
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
    protected void onDestroy() {
        super.onDestroy();
        removeObservers();
        EventBus.getDefault().unregister(this);
        globalEventQueue.setEventBusReady(false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     *  Injects the dependencies
     */
    private void injectDependencies() {
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
    }

}

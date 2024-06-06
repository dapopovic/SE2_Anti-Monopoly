package at.aau.anti_mon.client.activities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Context;
import android.content.Intent;
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
import at.aau.anti_mon.client.networking.MessagingService;
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
    private RecyclerView recyclerView;
    private LobbyUserAdapter userAdapter;

    ///////////////////////////////////// Variablen
    private String pin;
    private User user;
    private boolean leftLobby = false;
    private boolean gameStarted = false;
    private final ArrayList<User> userList = new ArrayList<>();

    ///////////////////////////////////// Networking

    @Inject WebSocketClient webSocketClient;
    @Inject GlobalEventQueue globalEventQueue;
    @Inject LobbyViewModel lobbyViewModel;
  //  @Inject JsonDataManager jsonDataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        injectDependencies();
        initializeUI();
        setupRecyclerView();
        processIntentAfterCreate();
        setupLiveDataObservers();
    }

    /**
     *  Injects the dependencies
     */
    private void injectDependencies() {
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
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
        textViewPin = findViewById(R.id.Pin);
        startButton = findViewById(R.id.lobby_start_game);
        readyButton = findViewById(R.id.lobby_ready);
    }

    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new LobbyUserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
    }

    private void processIntentAfterCreate() {
        Intent intent = getIntent();
        if (intent.hasExtra("username")) {
            user = new User(intent.getStringExtra("username"),
                    intent.getBooleanExtra("isOwner", false),
                    intent.getBooleanExtra("isReady", false)
            );
            addUserToTable(user);
        } else {
            Log.e(DEBUG_TAG, "Old Intent has no username");
            Toast.makeText(this, "Username missing", Toast.LENGTH_LONG).show();
            finish();
            //TODO Error Handling
        }

        if (intent.hasExtra("pin")) {
            pin = intent.getStringExtra("pin");
            textViewPin.setText(pin);
        } else {
            Log.e(DEBUG_TAG, "Old Intent has no pin");
            Toast.makeText(this, "PIN missing", Toast.LENGTH_LONG).show();
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
        startActivity(intent);
    }


    private void setupLiveDataObservers() {
        lobbyViewModel.getUserJoinedLiveData().observe(this, this::addUserToTable);
        lobbyViewModel.getUserLeftLiveData().observe(this, this::removeUserFromTable);
        lobbyViewModel.getReadyUpLiveData().observe(this, this::onReadyEvent);
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
        MessagingService.createUserMessage(user.getUsername(), pin, Commands.READY).sendMessage();
    }

    /**
     * On start game button click send message to server
     */
    public void onStartGame(View view) {
        MessagingService.createUserMessage(user.getUsername(), pin, Commands.START_GAME).sendMessage();
    }

    public void onCancelLobby(View view) {
        leaveLobby();
        finish();
    }

    private void leaveLobby() {
        MessagingService.createUserMessage(user.getUsername(), pin, Commands.LEAVE_GAME).sendMessage();
        leftLobby = true;
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
            webSocketClient.setUserID(user.getUsername());
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
        //globalEventQueue.setEventBusReady(false);
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



}

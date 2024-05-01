package at.aau.anti_mon.client.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.multidex.MultiDex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.activityGamefield;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.events.UserLeftLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

/**
 * LobbyActivity class to handle the lobby of the game
 */
public class LobbyActivity extends AppCompatActivity{


    //////////////////////////////////// Android UI
    TextView textView_pin;
    TextView[] userTextViews;

    HashMap<TextView, Boolean> availableTextViews = new HashMap<>();


    ///////////////////////////////////// Variablen
    public String pin;
    public String username = "TESTTESTTEST";

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

    private LobbyViewModel lobbyViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        lobbyViewModel = new ViewModelProvider(this).get(LobbyViewModel.class);

        // TODO:
        // SharedPreferences für zu speichernde Key-Value Paare
        sharedPreferences = getSharedPreferences(username, MODE_PRIVATE);

        // Setup der UI und andere Initialisierungen
        initializeUI();

        setupLiveDataObservers();

        // Nachdem die UI initialisiert wurde und der EventBus registriert ist, Netzwerkdienste starten
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

        userTextViews = new TextView[]{
                findViewById(R.id.coll1),
                findViewById(R.id.coll2),
                findViewById(R.id.coll3),
                findViewById(R.id.coll4),
                findViewById(R.id.coll5),
                findViewById(R.id.coll6),
        };

        // Alle TextViews als verfügbar markieren:
        for (TextView tv : userTextViews) {
            availableTextViews.put(tv, true);
        }

        textView_pin = findViewById(R.id.Pin);

        processIntent();
    }

    private void processIntent() {
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
            addUserToTable(username);
        } else {
            Log.e("ANTI-MONOPOLY-DEBUG", "Old Intent has no username");
            // Todo: Error handling
        }
        if(getIntent().hasExtra("pin")) {
            pin = getIntent().getStringExtra("pin");
            textView_pin.setText(pin);
        } else {
            Log.e("ANTI-MONOPOLY-DEBUG", "Old Intent has no pin");
            // Todo: Error handling
        }
    }

    private void addUserToTable(String username) {
        for (Map.Entry<TextView, Boolean> entry : availableTextViews.entrySet()) {
            if (entry.getValue()) {  // Prüfe, ob der TextView verfügbar ist
                entry.getKey().setText(username);

                Log.d("ANTI-MONOPOLY-DEBUG", "Added user to table: " + username);

                availableTextViews.put(entry.getKey(), false);  // Markiere als besetzt
                return;
            }
        }
        Log.e("LobbyActivity", "Kein verfügbarer Platz für neuen Benutzer.");
    }

    private void removeUserFromTable(String username) {
        for (Map.Entry<TextView, Boolean> entry : availableTextViews.entrySet()) {
            if (entry.getKey().getText().toString().equals(username)) {
                entry.getKey().setText("");
                availableTextViews.put(entry.getKey(), true);  // Markiere als verfügbar
                return;
            }
        }
        Log.e("ANTI-MONOPOLY-DEBUG", "Benutzername nicht gefunden.");
    }

    private void setPin(String pin) {
        textView_pin.setText(pin);
        Log.d("ANTI-MONOPOLY-DEBUG", "Pin set to: " + pin);

    }


    /**
     * Events for non-UI related global events
     * @param event UserLeftLobbyEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLeftLobbyEvent(UserLeftLobbyEvent event) {

        Log.d("ANTI-MONOPOLY-DEBUG", "UserLeftLobbyEvent");

        // TEST LiveData and Observer Pattern
        removeUserFromTable(event.getName());
    }

    /**
     * Events for non-UI related global events
     * @param event UserJoinedLobbyEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserJoinedLobbyEvent(UserJoinedLobbyEvent event) {

        Log.d("ANTI-MONOPOLY-DEBUG", "UserJoinedLobbyEvent");


        // TEST LiveData and Observer Pattern
        addUserToTable(event.getName());
    }

    /**
     * TODO: TEST
     * Heartbeat to keep connection alive
     * @param event HeartBeatEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {

        Log.d("ANTI-MONOPOLY-DEBUG", "HeartBeatEvent");


        JsonDataDTO jsonData = new JsonDataDTO(Commands.HEARTBEAT, new HashMap<>());
        jsonData.putData("msg", "PONG");
        String jsonMessage = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonMessage);
    }

    /**
     * TEST LiveData and Observer Pattern
     */
    private void setupLiveDataObservers() {

        // User joined: -> LiveData
        lobbyViewModel.getUserJoinedLiveData().observe(this, this::addUserToTable);

        // User left: -> LiveData
        lobbyViewModel.getUserLeftLiveData().observe(this, this::removeUserFromTable);

        // User Created Game: -> LiveData
        lobbyViewModel.getGameCreatedLiveData().observe(this, this::setPin);
    }

    public void onCancelLobby(View view) {
        // Leave Lobby
        JsonDataDTO jsonData = new JsonDataDTO(Commands.LEAVE_GAME, new HashMap<>());
        jsonData.putData("username", username);
        jsonData.putData("pin", pin);
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.println(Log.DEBUG, "ANTI-MONOPOLY-DEBUG", " Username sending to leave Lobby:" + jsonDataString);

        // Go back to last Activity on Stack (JoinGameActivity)
        finish();
    }

    public void onStartGame(View view) {
        // open Activity to start the game
        Intent intent = new Intent(this, activityGamefield.class);
        startActivity(intent);

    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.d("ANTI-MONOPOLY-DEBUG", "EventBus registered");
        globalEventQueue.setEventBusReady(true);

        if (webSocketClient != null) {
            webSocketClient.setUserId(username);
            webSocketClient.connectToServer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
        EventBus.getDefault().unregister(this);
        Log.d("ANTI-MONOPOLY-DEBUG", "EventBus unregistered");
        globalEventQueue.setEventBusReady(false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}

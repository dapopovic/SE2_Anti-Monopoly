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
import androidx.multidex.MultiDex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.events.UserLeftLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;

/**
 * LobbyActivity class to handle the lobby of the game
 */
public class LobbyActivity extends AppCompatActivity{


    //////////////////////////////////// Android UI
    TextView textViewPin;
    TextView[] userTextViews;

    HashMap<TextView, Boolean> availableTextViews = new HashMap<>();


    ///////////////////////////////////// Variablen
    int numberOfUsers = 0;
    private String pin;
    private String username;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        // TODO:
        // SharedPreferences für zu speichernde Key-Value Paare
        sharedPreferences = getSharedPreferences(username, MODE_PRIVATE);

        // Setup der UI und andere Initialisierungen
        initializeUI();

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

        textViewPin = findViewById(R.id.Pin);

        processIntent();
    }

    private void processIntent() {
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
            addUserToTable(username);
        } else {
            Log.e(this.getLocalClassName(), "Old Intent has no username");
            // Todo: Error handling
        }
        if(getIntent().hasExtra("pin")) {
            pin = getIntent().getStringExtra("pin");
            textViewPin.setText(pin);
        } else {
            Log.e(this.getLocalClassName(), "Old Intent has no pin");
            // Todo: Error handling
        }
    }

    private void addUserToTable(String username) {
        for (Map.Entry<TextView, Boolean> entry : availableTextViews.entrySet()) {
            if (entry.getValue()) {  // Prüfe, ob der TextView verfügbar ist
                entry.getKey().setText(username);
                availableTextViews.put(entry.getKey(), false);  // Markiere als besetzt
                return;
            }
        }
        Log.e(this.getLocalClassName(), "Kein verfügbarer Platz für neuen Benutzer.");
    }

    private void removeUserFromTable(String username) {
        for (Map.Entry<TextView, Boolean> entry : availableTextViews.entrySet()) {
            if (entry.getKey().getText().toString().equals(username)) {
                entry.getKey().setText("");
                availableTextViews.put(entry.getKey(), true);  // Markiere als verfügbar
                return;
            }
        }
        Log.e(this.getLocalClassName(), "Benutzername nicht gefunden.");
    }


    /**
     * TODO: Use HashMap to store the users and use operations to add and remove users
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLeftLobbyEvent(UserLeftLobbyEvent event) {
        removeUserFromTable(event.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserJoinedLobbyEvent(UserJoinedLobbyEvent event) {
        addUserToTable(event.getName());
    }

    public void onCancelLobby(View view) {
        // -> go back to JoinGame/StartNewGameActivity
        Intent intent = new Intent(this, StartNewGameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.HEARTBEAT, new HashMap<>());
        jsonData.putData("msg", "PONG");
        String jsonMessage = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonMessage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.d(this.getLocalClassName(), "EventBus registered");
        globalEventQueue.setEventBusReady(true);

        if (webSocketClient != null) {
            webSocketClient.connectToServer();
        }
    }

    @Override
    protected void onStop() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }

        EventBus.getDefault().unregister(this);
        Log.d(this.getLocalClassName(), "EventBus unregistered");
        globalEventQueue.setEventBusReady(false);
        super.onStop();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}

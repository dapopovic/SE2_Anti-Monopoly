package at.aau.anti_mon.client.activities;

import android.content.Context;
import android.content.Intent;
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

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.PinReceivedEvent;
import at.aau.anti_mon.client.events.ReceiveMessageEvent;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.events.UserLeftLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.networking.WebSocketMessageHandler;

/**
 * LobbyActivity class to handle the lobby of the game
 */
public class LobbyActivity extends AppCompatActivity{


    //////////////////////////////////// Android UI
    TextView textView_pin;
    //TextView[] userTextViews;

    TextView textView_coll1;
    TextView textView_coll2;
    TextView textView_coll3;
    TextView textView_coll4;
    TextView textView_coll5;



    ///////////////////////////////////// Variablen
    int numberOfUsers = 0;
    public String pin;
    public String username;

    ///////////////////////////////////// Networking

    /**
     * Dependency Injection of WebSocketClient
     */
    @Inject
    WebSocketClient webSocketClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /// UI

        textView_pin = findViewById(R.id.Pin);
        /*userTextViews = new TextView[]{
                findViewById(R.id.coll1),
                findViewById(R.id.coll2),
                findViewById(R.id.coll3),
                findViewById(R.id.coll4),
                findViewById(R.id.coll5)
        };
         */
        textView_coll1 = findViewById(R.id.coll1);
        textView_coll2 = findViewById(R.id.coll2);
        textView_coll3 = findViewById(R.id.coll3);
        textView_coll4 = findViewById(R.id.coll4);
        textView_coll5 = findViewById(R.id.coll5);

        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
        EventBus.getDefault().register(this);
        Log.d("LobbyActivity", "EventBus registered");



        //////////////////////////////////////////////////  Empfange Daten von StartNewGameActivity / JoinGameActivity
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
            textView_coll1.setText(username);
        } else {
            Log.e("LobbyActivity", "Old Intent has no username");
        }

        if(getIntent().hasExtra("pin")) {
            pin = getIntent().getStringExtra("pin");
            textView_pin.setText(pin);
        } else {
            Log.e("LobbyActivity", "Old Intent has no pin");
        }
        ///////////////////////////////////////////////////

    }


    /**
     * TODO: Use HashMap to store the users and use operations to add and remove users
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUserLeftLobbyEvent(UserLeftLobbyEvent event) {
        if (numberOfUsers > 1) {
            //userTextViews[numberOfUsers].setText("");
            numberOfUsers--;
            Log.w("LobbyActivity", "Number of users: " + numberOfUsers);
        }else {
            Log.w("LobbyActivity", "Minimum number of users reached.");
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUserJoinedLobbyEvent(UserJoinedLobbyEvent event) {
        if (numberOfUsers < 6){  //userTextViews.length - 1) {
            //userTextViews[numberOfUsers + 1].setText(event.getName());
            numberOfUsers++;
            Log.w("LobbyActivity", "Number of users: " + numberOfUsers);

        }else {
            Log.w("LobbyActivity", "Maximum number of users reached.");
        }
    }

    /**
     * Event to receive a message
     * -> MAIN Thread to update UI
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onPinReceivedEvent(PinReceivedEvent event) {
        Log.d("LobbyActivity", "Pin received: " + event.getPin());
        if (!isFinishing()) {
            textView_pin.setText(event.getPin());
            Log.d("LobbyActivity", "Pin set on textView: " + event.getPin());
        } else {
            Log.d("LobbyActivity", "Activity is finishing. Cannot set pin.");
        }
    }

    public void onCancelLobby(View view) {
        // -> go back to JoinGame/StartNewGameActivity
        Intent intent = new Intent(LobbyActivity.this, getIntent().getClass());
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

    @Override
    protected void onStart() {
        super.onStart();
        if (webSocketClient != null) {
            webSocketClient.connectToServer();
        }

        // Eventbus wird in OnCreate registriert

    }

    @Override
    protected void onStop() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
        EventBus.getDefault().unregister(this);
        Log.d("LobbyActivity", "EventBus unregistered");
        super.onStop();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}

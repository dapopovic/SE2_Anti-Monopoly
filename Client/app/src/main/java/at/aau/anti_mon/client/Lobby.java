package at.aau.anti_mon.client;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.CreatedGameEvent;
import at.aau.anti_mon.client.events.ReceiveMessageEvent;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.events.UserLeftLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;

public class Lobby extends AppCompatActivity {


    /**
     * Aktualisierungen NICHT über statische Variablen / Methoden in Android...
     */


    //////////////////////////////////// Android UI
    TextView textView_pin;
    TextView textView_coll1;
    TextView textView_coll2;
    TextView textView_coll3;
    TextView textView_coll4;


    ///////////////////////////////////// Variablen
    int numberOfUser = 0;
    public String pin;
    public String username;
    private CommandFactory commandFactory;





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

        /*
         * Eventbus registrieren:
         */
        EventBus.getDefault().register(this);

        commandFactory = new CommandFactory();

        // Username aus Intent-Extra holen
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        textView_pin = findViewById(R.id.Pin);
        textView_pin.setText(pin);

        // Schlecht implementiert, da man so keine User hinzufügen kann
        // ( Ich kann nicht ( bzw. schon, aber nicht sauber) durch die TextViews durchiterieren)
        // Außerdem wird der View trotzdem bereits gesetzt.
         textView_coll1 = findViewById(R.id.coll1);
         textView_coll2 = findViewById(R.id.coll2);
         textView_coll3 = findViewById(R.id.coll3);
         textView_coll4 = findViewById(R.id.coll4);

         textView_coll1.setText(username);

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserLeftLobbyEvent event) {
        this.textView_pin.setText(event.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserJoinedLobbyEvent event) {
        // Dynamische Aktualisierung der Teilnehmer
        if (numberOfUser == 0) {
            textView_coll2.setText(event.getName());
        } else if (numberOfUser == 1) {
            textView_coll3.setText(event.getName());
        } else if (numberOfUser == 2) {
            textView_coll4.setText(event.getName());
        }
        numberOfUser++;
    }



    public void onCancelLobby(View view) {
        Intent intent = new Intent(Lobby.this, StartNewGameActivity.class);
        startActivity(intent);
    }
}

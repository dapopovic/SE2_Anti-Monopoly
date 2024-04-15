package at.aau.anti_mon.client.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
import at.aau.anti_mon.client.events.ReceiveMessageEvent;
import at.aau.anti_mon.client.events.SendMessageEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;

/**
 *
 */
public class StartNewGameActivity extends AppCompatActivity {

    EditText usernameEditText;

    @Inject
    WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_new_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.username);

        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);

    }

    public void onCreateGameClicked(View view) {
        String username = usernameEditText.getText().toString();

        if (!username.isEmpty()) {
            /*
             * Communication with Server -> send username to server -> get Pin
             */
            JsonDataDTO jsonData = new JsonDataDTO(Commands.CREATE_GAME, new HashMap<>());
            jsonData.putData("name", username);
            String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
            webSocketClient.sendMessageToServer(jsonDataString);
            Log.println(Log.DEBUG, "Network", " Username sending for pin:" + jsonDataString);


            // Den Username an die LobbyActivity übergeben:
            Intent intent = new Intent(this, LobbyActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    public void onCancelStartNewGame(View view) {
        Intent intent = new Intent(StartNewGameActivity.this, StartMenuActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (webSocketClient != null) {
            webSocketClient.connectToServer();
        }
    }

    @Override
    protected void onStop() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
        super.onStop();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

package at.aau.anti_mon.client.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.multidex.MultiDex;

import java.util.HashMap;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.networking.WebSocketClient;

public class JoinGameActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText pinEditText;

    @Inject WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        usernameEditText = findViewById(R.id.editText_joinGame_Name);
        pinEditText = findViewById(R.id.editText_joinGame_Pin);

        injectDependencies();
    }

    public void onJoinGameClicked(View view) {
        String username = usernameEditText.getText().toString();
        String pin = pinEditText.getText().toString();

        // add Name to Websocket URI
        webSocketClient.setUserID(username);
        if (!webSocketClient.isConnected()){
            webSocketClient.connectToServer();
        }
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
        }
        if (pin.isEmpty()) {
            pinEditText.setError("Pin is required");
        }


        // TODO: Test of the new MessagingService
        //JsonDataManager.createUserMessage(username, pin, Commands.JOIN).sendMessage();
        MessagingService.createUserMessage(username, pin, Commands.JOIN).sendMessage();




        //JsonDataDTO jsonData = new JsonDataDTO(Commands.JOIN, new HashMap<>());
        //jsonData.putData("username", username);
        //jsonData.putData("pin", pin);
        //String jsonMessage = JsonDataManager.createJsonMessage(jsonData);
        //webSocketClient.sendMessageToServer(jsonMessage);


        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("pin", pin);
        startActivity(intent);
    }


    /**
     * Go back to last Activity on Stack (StartMenuActivity)
     */
    public void onCancelJoinGame(View view) {
        finish();
    }

    /**
     * Injection des WebSocketClients
     */
    public void injectDependencies() {
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
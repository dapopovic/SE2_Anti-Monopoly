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

import java.util.HashMap;
import java.util.logging.Logger;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;

/**
 *
 */
public class StartNewGameActivity extends AppCompatActivity {

    EditText usernameEditText;

    String pin;

    @Inject
    WebSocketClient webSocketClient;

    @Inject
    CreateGameViewModel createGameViewModel;
    /**
     * Dependency Injection of GlobalEventQueue
     */
    @Inject
    GlobalEventQueue globalEventQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setupLiveDataObservers();
    }

    private void setupLiveDataObservers() {
        createGameViewModel.getPinLiveData().observe(this, this::onPinReceived);
    }


    /**
     * Event when the button "Create Game" is clicked
     *
     * @param view
     */
    public void onCreateGameClicked(View view) {

        String username = usernameEditText.getText().toString();
        webSocketClient.setUserId(username);

        if (username.isEmpty()) {
            usernameEditText.setError("Please enter a username");
            return;
        }
        // Senden des Usernames zum Server, um eine neue Spielsession zu starten und einen Pin zu erhalten
        JsonDataDTO jsonData = new JsonDataDTO(Commands.CREATE_GAME, new HashMap<>());
        jsonData.putData("username", username);
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.println(Log.DEBUG, "ANTI-MONOPOLY-DEBUG", "Username sending for pin: " + jsonDataString);
    }

    public void onPinReceived(String receivedPin) {
        Log.d("ANTI-MONOPOLY-DEBUG", "Pin received: " + receivedPin);
        if (!isFinishing()) {
            pin = receivedPin;
            startLobbyActivity(usernameEditText.getText().toString(), pin);
        } else {
            Log.d("ANTI-MONOPOLY-DEBUG", "Activity is finishing. Cannot set pin.");
        }
    }

    private void startLobbyActivity(String username, String pin) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("pin", pin);
        intent.putExtra("isOwner", true);
        startActivity(intent);
    }

    public void onCancelStartNewGame(View view) {
        // Go back to last Activity on Stack (StartMenuActivity)
        createGameViewModel.getPinLiveData().removeObservers(this);
        createGameViewModel.getPinLiveData().setPending(false);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pin = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        pin = null;
        createGameViewModel.getPinLiveData().removeObservers(this);
        createGameViewModel.getPinLiveData().setPending(false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

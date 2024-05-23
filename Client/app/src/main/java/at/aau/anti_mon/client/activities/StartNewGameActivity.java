package at.aau.anti_mon.client.activities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.multidex.MultiDex;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;

/**
 *
 */
public class StartNewGameActivity extends AppCompatActivity {

    @Inject  WebSocketClient webSocketClient;
    @Inject  CreateGameViewModel createGameViewModel;
    EditText usernameEditText;
    String pin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_new_game);
        initializeUI();
        injectDependencies();
        setupLiveDataObservers();
    }

    private void injectDependencies() {
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
    }

    private void initializeUI() {
        usernameEditText = findViewById(R.id.username);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), this::applyWindowInsets);
    }

    private WindowInsetsCompat applyWindowInsets(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }

    private void setupLiveDataObservers() {
        createGameViewModel.getPinLiveData().observe(this, this::onPinReceived);
    }
    private void removeObservers() {
        createGameViewModel.getPinLiveData().removeObservers(this);
        createGameViewModel.getPinLiveData().setPending(false);
    }

    /**
     * Event when the button "Create Game" is clicked
     * @param view
     */
    public void onCreateGameClicked(View view) {
        String username = usernameEditText.getText().toString();
        webSocketClient.setUserID(username);

        if (username.isEmpty()) {
            usernameEditText.setError("Please enter a username");
            return;
        }

        JsonDataManager.createUserMessage(username, Commands.CREATE_GAME).sendMessage();

        if (!webSocketClient.isConnected()) {
            Toast toast = Toast.makeText(this, "No connection to server", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onPinReceived(String receivedPin) {
        Log.d(DEBUG_TAG, "Pin received: " + receivedPin);
        if (isFinishing()) {
            Log.d(DEBUG_TAG, "Activity is finishing. Cannot set pin.");
            return;
        }
        pin = receivedPin;
        startLobbyActivity(usernameEditText.getText().toString(), pin);
    }

    private void startLobbyActivity(String username, String pin) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("pin", pin);
        intent.putExtra("isOwner", true);
        intent.putExtra("isReady", true);
        startActivity(intent);
    }

    public void onCancelStartNewGame(View view) {
        // Go back to last Activity on Stack (StartMenuActivity)
        removeObservers();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pin = null;
        if (!createGameViewModel.getPinLiveData().hasActiveObservers()) {
            setupLiveDataObservers();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        webSocketClient.connectToServer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pin = null;
        removeObservers();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

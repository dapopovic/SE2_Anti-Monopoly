package at.aau.anti_mon.client.ui.creategame;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

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

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;
import at.aau.anti_mon.client.utilities.MessagingUtility;

/**
 *
 */
public class CreateGameActivity extends AppCompatActivity {

    EditText usernameEditText;

    String pin;

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
        setContentView(R.layout.activity_create_game);
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
    private void removeObservers() {
        createGameViewModel.getPinLiveData().removeObservers(this);
        createGameViewModel.getPinLiveData().setPending(false);
    }

    /**
     * Event when the button "Create Game" is clicked
     *
     * @param view
     */
    public void onCreateGameClicked(View view) {
        String username = usernameEditText.getText().toString();
        MessagingUtility.connectToServerWithUserID(username);
        if (username.isEmpty()) {
            usernameEditText.setError("Please enter a username");
            return;
        }
        MessagingUtility.createUserMessage(username, Commands.CREATE_GAME).sendMessage();
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

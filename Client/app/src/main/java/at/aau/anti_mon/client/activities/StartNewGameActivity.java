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
import at.aau.anti_mon.client.R;;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.PinReceivedEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;

/**
 *
 */
public class StartNewGameActivity extends AppCompatActivity {

    EditText usernameEditText;

    String pin;

    @Inject
    WebSocketClient webSocketClient;

    /**
     * Dependency Injection of GlobalEventQueue
     */
    @Inject
    GlobalEventQueue globalEventQueue;


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


    /**
     * Event when the button "Create Game" is clicked
     * Todo: better Intent handling
     * @param view
     */
    public void onCreateGameClicked(View view) {

        String username = usernameEditText.getText().toString();
        webSocketClient.setUserId(username);

        if (!username.isEmpty() && pin == null) {
            // Senden des Usernames zum Server, um eine neue Spielsession zu starten und einen Pin zu erhalten
            JsonDataDTO jsonData = new JsonDataDTO(Commands.CREATE_GAME, new HashMap<>());
            jsonData.putData("username", username);
            String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
            webSocketClient.sendMessageToServer(jsonDataString);
            Log.println(Log.DEBUG, "ANTI-MONOPOLY-DEBUG", "Username sending for pin: " + jsonDataString);
        } else if (pin != null) {
            // Wenn der Pin schon vorhanden ist, direkt die LobbyActivity starten
            startLobbyActivity(username, pin);
        }
    }

    /**
     * Event when the pin is received from the server
     * -> MAIN Thread to update UI
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPinReceivedEvent(PinReceivedEvent event) {
        Log.d("ANTI-MONOPOLY-DEBUG", "Pin received: " + event.getPin());
        if (!isFinishing()) {
            pin = event.getPin();
            startLobbyActivity(usernameEditText.getText().toString(), pin);
        } else {
            Log.d("ANTI-MONOPOLY-DEBUG", "Activity is finishing. Cannot set pin.");
        }
    }

    private void startLobbyActivity(String username, String pin) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("pin", pin);
        startActivity(intent);
    }

    public void onCancelStartNewGame(View view) {
        // Go back to last Activity on Stack (StartMenuActivity)
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.d("ANTI-MONOPOLY-DEBUG", "EventBus registered");
        //globalEventQueue.setEventBusReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d("ANTI-MONOPOLY-DEBUG", "EventBus unregistered");
        //globalEventQueue.setEventBusReady(false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

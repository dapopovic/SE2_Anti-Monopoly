package at.aau.anti_mon.client;

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

    private WebSocketClient networkHandler;
    private CommandFactory commandFactory;

    EditText usernameEditText;

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


        /*
         * EventBus für Messages registrieren
         */
        EventBus.getDefault().register(this);
        networkHandler = new WebSocketClient();
        commandFactory = new CommandFactory();

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(ReceiveMessageEvent event) {
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(event.getMessage());
        Commands commandEnum = jsonDataDTO.getCommand();
        Command command = commandFactory.getCommand(commandEnum.getCommand());
        if (command != null) {
            command.execute(jsonDataDTO);
        } else {
            Log.w("Network", "Unbekannter oder nicht unterstützter Befehl.");
        }
    }



    public void onCreateGameClicked(View view) {
        String username = usernameEditText.getText().toString();

        if (!username.isEmpty()) {
            JsonDataDTO jsonData = new JsonDataDTO(Commands.CREATE_GAME, new HashMap<>());
            jsonData.putData("name", username);
            String jsonMessage = JsonDataManager.createJsonMessage(jsonData);

            EventBus.getDefault().post(new SendMessageEvent(jsonMessage));

            // Den Username nutzen:
            Intent intent = new Intent(this, Lobby.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    public void onCancelStartNewGame(View view) {
        Intent intent = new Intent(StartNewGameActivity.this, Start_Page.class);
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

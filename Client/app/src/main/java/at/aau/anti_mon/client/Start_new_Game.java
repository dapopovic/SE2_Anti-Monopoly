package at.aau.anti_mon.client;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import at.aau.anti_mon.client.networking.WebSocketClient;

public class Start_new_Game extends AppCompatActivity {

    WebSocketClient networkHandler;
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

        networkHandler = new WebSocketClient();
        networkHandler.connectToServer(this::messageReceivedFromServer);

    }

    private void messageReceivedFromServer(String message) {
        // TODO handle received messages
        Log.d("Network", message);
        Lobby.Pin = message;
    }

    public void onCancel_StartNewGame(View view) {
        Intent intent = new Intent(Start_new_Game.this, Start_Page.class);
        startActivity(intent);
    }

    public void Createsng(View view) {
        EditText username = findViewById(R.id.username);
        String name = username.getText().toString();
        if(!name.isEmpty()){
            Lobby.username = name;
            Data d = new Data(name);
            CreateGame createGame = new CreateGame(Commands.CREATE_GAME,d);
            Gson gson = new Gson();
            networkHandler.sendMessageToServer(gson.toJson(createGame));
            Intent intent = new Intent(Start_new_Game.this, Lobby.class);
            startActivity(intent);
        }
    }
}

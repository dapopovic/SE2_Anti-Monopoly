package at.aau.anti_mon.client.ui.joingame;

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

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.utilities.MessagingUtility;

public class JoinGameActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText pinEditText;

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

    }

    public void onJoinGameClicked(View view) {
        String username = usernameEditText.getText().toString();
        String pin = pinEditText.getText().toString();

        // add Name to Websocket URI
        MessagingUtility.connectToServerWithUserID(username);
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
        }
        if (pin.isEmpty()) {
            pinEditText.setError("Pin is required");
        }

        MessagingUtility.createUserMessage(username,pin,Commands.JOIN_GAME).sendMessage();

        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("pin", pin);
        startActivity(intent);
    }

    public void onCancelJoinGame(View view) {
        // Go back to last Activity on Stack (StartMenuActivity)
        finish();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
package at.aau.anti_mon.client.ui.startmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.ui.creategame.CreateGameActivity;
import at.aau.anti_mon.client.ui.instructions.GameInstructionsActivity;
import at.aau.anti_mon.client.ui.joingame.JoinGameActivity;
import at.aau.anti_mon.client.ui.loadgame.LoadGameActivity;

public class StartMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void onStartGame(View view) {
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

    public void onJoinGame(View view) {
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }

    public void loadGame(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        startActivity(intent);
    }

    public void gameInstructions(View view) {
        Intent intent = new Intent(this, GameInstructionsActivity.class);
        startActivity(intent);
    }
}


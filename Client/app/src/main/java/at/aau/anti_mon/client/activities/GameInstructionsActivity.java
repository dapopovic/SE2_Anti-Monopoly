package at.aau.anti_mon.client.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.activityGamefield;

public class GameInstructionsActivity extends AppCompatActivity {
    public static String username;
    public static String Pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        TextView pincode = findViewById(R.id.Pin);
        pincode.setText(Pin);

        TextView coll1 = findViewById(R.id.coll1);
        coll1.setText(username);
    }

    public void onStartGame(View view) {
        Intent intent = new Intent(GameInstructionsActivity.this, activityGamefield.class);
        startActivity(intent);
    }

    public void onCancelGameInstructions(View view) {
        Intent intent = new Intent(GameInstructionsActivity.this, StartNewGameActivity.class);
        startActivity(intent);
    }
}

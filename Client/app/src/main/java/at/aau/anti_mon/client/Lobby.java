package at.aau.anti_mon.client;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Lobby extends AppCompatActivity {
    public static String username;
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

        Button cancel = findViewById(R.id.lobby_cancel);
        cancel.setOnClickListener(
                v ->{
                    Intent intent = new Intent(Lobby.this, Start_new_Game.class);
                    startActivity(intent);
                });

        TextView coll1 = findViewById(R.id.coll1);
        coll1.setText(username);

        //Ist nur zum zeigen wie es auschaut wenn mehrere spieler beitretten
        TextView coll2 = findViewById(R.id.coll2);
        coll2.setText(username);
        TextView coll3 = findViewById(R.id.coll3);
        coll3.setText(username);
        TextView coll4 = findViewById(R.id.coll4);
        coll4.setText(username);
        TextView coll5 = findViewById(R.id.coll5);
        coll5.setText(username);
        TextView coll6 = findViewById(R.id.coll6);
        coll6.setText(username);
    }
}

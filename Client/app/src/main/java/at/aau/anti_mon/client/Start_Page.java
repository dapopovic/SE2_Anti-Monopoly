package at.aau.anti_mon.client;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import at.aau.anti_mon.client.networking.WebSocketClient;

public class Start_Page extends AppCompatActivity {

    //public static WebSocketClient networkHandler;
    //public static String Receivedmessage;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //networkHandler = new WebSocketClient();
        //networkHandler.connectToServer(this::messageReceivedFromServer);

        Button newGame = findViewById(R.id.start_new_game);
        newGame.setOnClickListener(
                v ->{
                    Intent intent = new Intent(Start_Page.this, Start_new_Game.class);
                    startActivity(intent);
        });
    }
    /*public void messageReceivedFromServer(String message){
        // TODO handle received messages
        Log.d("Network", message);
        Receivedmessage = message;
    }*/
}


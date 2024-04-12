package at.aau.anti_mon.client;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.greenrobot.eventbus.EventBus;

import at.aau.anti_mon.client.networking.WebSocketClient;

public class MainActivity extends AppCompatActivity {

    TextView textViewServerResponse;

    WebSocketClient networkHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EventBus.getDefault().register(this);


        textViewServerResponse = findViewById(R.id.textViewResponse);

        networkHandler = new WebSocketClient();
        networkHandler.connectToServer();

        Button toMainMenu = findViewById(R.id.buttonToStartPage);
        toMainMenu.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, Start_Page.class);
                    startActivity(intent);
                });

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
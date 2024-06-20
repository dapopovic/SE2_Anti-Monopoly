package at.aau.anti_mon.client.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

import javax.inject.Inject;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.networking.WebSocketClient;

public class PopActivitySettings extends PopActivityObjects {

    @Inject
    WebSocketClient webSocketClient;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_settings);

        Button surrender = findViewById(R.id.btnsurrender);

        surrender.setOnClickListener(v ->{
            Intent resultIntent = new Intent();
            resultIntent.putExtra("setting", "surrender");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        Button exitgame = findViewById(R.id.btnexitgame);

        exitgame.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("setting", "exitgame");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}
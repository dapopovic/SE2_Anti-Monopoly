package at.aau.anti_mon.client.ui.popups;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import at.aau.anti_mon.client.R;

public class PopActivitySettings extends BasePopUp {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_settings);

        Intent intent = getIntent();
        boolean isOwner = intent.getBooleanExtra("at.aau.anti_mon.client.isOwner", false);

        Button endgame = findViewById(R.id.btnendgame);
        if(isOwner){
            Log.d(DEBUG_TAG,"PopUpActivitySettings - Owner is true");
            endgame.setVisibility(View.VISIBLE);
            endgame.setOnClickListener(v -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("at.aau.anti_mon.client.setting", "endgame");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            });
        }
        if(!isOwner){
            endgame.setVisibility(View.INVISIBLE);
        }

        Button surrender = findViewById(R.id.btnsurrender);

        surrender.setOnClickListener(v ->{
            Intent resultIntent = new Intent();
            resultIntent.putExtra("at.aau.anti_mon.client.setting", "surrender");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        Button exitgame = findViewById(R.id.btnexitgame);

        exitgame.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("at.aau.anti_mon.client.setting", "exitgame");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        Button x = findViewById(R.id.X);
        x.setOnClickListener(v ->{
            Intent resultIntent = new Intent();
            resultIntent.putExtra("at.aau.anti_mon.client.setting", "x");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}
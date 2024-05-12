package at.aau.anti_mon.client.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Random;

import javax.inject.Inject;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;

public class PopActivityDice extends AppCompatActivity {
    @Inject
    WebSocketClient webSocketClient;
    ImageView dice1;
    ImageView dice2;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_dice);
        try {
            getActionBar().hide();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        dice1 = findViewById(R.id.dice1);
        dice2 = findViewById(R.id.dice2);

        dice1.setOnClickListener(v -> {
            rollDicesAndSendNumbersToServer();
        });

        dice2.setOnClickListener(v -> {
            rollDicesAndSendNumbersToServer();
        });
    }

    public void onX(View view) {
        finish();
    }

    // 1) on the beginning the user should be able to choose the figure
    // 2) in the middle of the game the user should know when he has to make a move
    private void rollDicesAndSendNumbersToServer() {
        int num1 = rollTheDice(dice1);
        int num2 = rollTheDice(dice2);

        // send the sum of two numbers to the server
        JsonDataDTO jsonData = new JsonDataDTO(Commands.DICE_NUMBER, new HashMap<>());
        jsonData.putData("number", String.valueOf(num1 + num2));
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        assert (webSocketClient != null && webSocketClient.isConnected());
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.d("NUMBERS", jsonDataString);
        finish();
    }

    private int rollTheDice(ImageView dice) {
        int randomNumber = random.nextInt(6) + 1;
        switch (randomNumber) {
            case 1:
                dice.setImageResource(R.drawable.eins);
                break;
            case 2:
                dice.setImageResource(R.drawable.zwei);
                break;
            case 3:
                dice.setImageResource(R.drawable.drei);
                break;
            case 4:
                dice.setImageResource(R.drawable.vier);
                break;
            case 5:
                dice.setImageResource(R.drawable.fuenf);
                break;
            case 6:
                dice.setImageResource(R.drawable.sechs);
                break;
        }
        return randomNumber;
    }
}
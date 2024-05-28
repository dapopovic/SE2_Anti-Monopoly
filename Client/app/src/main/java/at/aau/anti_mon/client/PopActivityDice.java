package at.aau.anti_mon.client;

import android.app.Activity;
import android.content.SharedPreferences;
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

import java.util.Random;

import at.aau.anti_mon.client.activities.ActivityGameField;

public class PopActivityDice extends Activity {

    ImageView dice1;
    ImageView dice2;
    Random random = new Random();

    int Zahl1 = 0;
    int Zahl2 = 0;
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
        dice1.setEnabled(true);
        dice2.setEnabled(true);

        dice1.setOnClickListener(v -> {
            rollTheDice1(dice1);
            rollTheDice2(dice2);
            dice1.setEnabled(false);
            dice2.setEnabled(false);
        });

        dice2.setOnClickListener(v -> {
            rollTheDice1(dice1);
            rollTheDice2(dice2);
            dice1.setEnabled(false);
            dice2.setEnabled(false);
        });
    }

    public void onX(View view) {
        Log.d("onX", "I am in onX from PopActivityDice");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(Zahl1!=0 && Zahl2 !=0){
            editor.putInt("zahl1", Zahl1);
            editor.putInt("zahl2", Zahl2);
            editor.putBoolean("Wurfel",true);
        }
        if(Zahl1==0 | Zahl2 ==0){
            editor.putBoolean("Wurfel",false);
        }
        editor.apply();
        finish();
    }

    private void rollTheDice1(ImageView dice) {
        Zahl1 = random.nextInt(6) + 1;
        Log.i("ROLLING", String.valueOf(Zahl1));
        switch (Zahl1) {
            case 1:
                dice.setImageResource(R.drawable.dice1);
                break;
            case 2:
                dice.setImageResource(R.drawable.dice2);
                break;
            case 3:
                dice.setImageResource(R.drawable.dice3);
                break;
            case 4:
                dice.setImageResource(R.drawable.dice4);
                break;
            case 5:
                dice.setImageResource(R.drawable.dice5);
                break;
            case 6:
                dice.setImageResource(R.drawable.dice6);
                break;
        }
    }
    private void rollTheDice2(ImageView dice) {
        Zahl2 = random.nextInt(6) + 1;
        Log.i("ROLLING", String.valueOf(Zahl2));
        switch (Zahl2) {
            case 1:
                dice.setImageResource(R.drawable.dice1);
                break;
            case 2:
                dice.setImageResource(R.drawable.dice2);
                break;
            case 3:
                dice.setImageResource(R.drawable.dice3);
                break;
            case 4:
                dice.setImageResource(R.drawable.dice4);
                break;
            case 5:
                dice.setImageResource(R.drawable.dice5);
                break;
            case 6:
                dice.setImageResource(R.drawable.dice6);
                break;
        }
    }
}
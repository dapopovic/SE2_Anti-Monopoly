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

import java.util.Random;

import at.aau.anti_mon.client.R;

public class PopActivityDice extends AppCompatActivity {

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
            rollTheDice(dice1);
            rollTheDice(dice2);
        });

        dice2.setOnClickListener(v -> {
            rollTheDice(dice1);
            rollTheDice(dice2);
        });
    }

    public void onX(View view) {
        finish();
    }

    private void rollTheDice(ImageView dice) {
        int randomNumber = random.nextInt(6) + 1;
        Log.i("ROLLING", String.valueOf(randomNumber));
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
    }
}
package at.aau.anti_mon.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.events.DiceNumbersEvent;
import at.aau.anti_mon.client.events.PinReceivedEvent;

public class ActivityGamefield extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gamefield);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        openDicesPopup();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDicesRolledEvent(DiceNumbersEvent event) {
        Log.d("ANTI-MONOPOLY-DEBUG", "Number from rolled dices received: " + event.getNumber());

        // move the corresponding figure
    }

    // implemented for later : the user has to make a move now
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void theUserIsOnMove() {
        Log.d("ANTI-MONOPOLY-DEBUG", "The user has to make a move now! Show PopUp window with the dice");
        openDicesPopup();
    }

    public void onSettings(View view) {
        Intent i = new Intent(getApplicationContext(),PopActivitySettings.class);
        startActivity(i);
    }

    public void onHandel(View view) {
        Intent i = new Intent(getApplicationContext(),PopActivityHandel.class);
        startActivity(i);
    }

    public void onObjects(View view) {
        Intent i = new Intent(getApplicationContext(),PopActivityObjects.class);
        startActivity(i);
    }

    public void onFinish(View view) {
        Intent i = new Intent(getApplicationContext(), StartMenuActivity.class);
        startActivity(i);
    }

    public void openDicesPopup() {
        Intent i = new Intent(getApplicationContext(), PopActivityDice.class);
        startActivity(i);
    }
}
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

import at.aau.anti_mon.client.R;

public class PopActivityBlameForCheating extends PopActivityObjects {
    private String cheatingPlayerName;
    public PopActivityBlameForCheating() {
        super(R.layout.activity_blame_for_cheating_popup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        cheatingPlayerName = intent.getStringExtra("PLAYER_NAME");

        // You can use playerName if needed within this activity
    }

    public void reportCheating(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultKey", "yes");
        returnIntent.putExtra("cheating_player_name", cheatingPlayerName);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void notReportCheating(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultKey", "no");
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
package at.aau.anti_mon.client.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;

public class PopActivityCheating extends PopActivityObjects {
    public PopActivityCheating() {
        super(R.layout.activity_pop_cheating);
    }

    public void letsCheat(View view) {
        Log.d("Cheating", "cheat!");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultKey", "yes");
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    public void notCheat(View view) {
        Log.d("Cheating", "not cheat...");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultKey", "no");
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
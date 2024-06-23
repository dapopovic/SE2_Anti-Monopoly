package at.aau.anti_mon.client.ui.popups;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import at.aau.anti_mon.client.R;

public class PopActivityCheating extends PopActivityObjects {
    public PopActivityCheating() {
        super(R.layout.popup_cheating);
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
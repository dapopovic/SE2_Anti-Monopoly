package at.aau.anti_mon.client.ui.popups;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import at.aau.anti_mon.client.R;

public class PopActivityHandel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_handel);
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
    }

    public void onX(View view) {
        finish();
    }
}
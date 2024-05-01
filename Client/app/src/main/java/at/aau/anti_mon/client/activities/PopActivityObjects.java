package at.aau.anti_mon.client.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Objects;

import at.aau.anti_mon.client.R;

public class PopActivityObjects extends Activity {
    private int layout = R.layout.activity_pop_objects;
    public PopActivityObjects() {
    }
    public PopActivityObjects(int layout) {
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        Objects.requireNonNull(getActionBar()).hide();

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
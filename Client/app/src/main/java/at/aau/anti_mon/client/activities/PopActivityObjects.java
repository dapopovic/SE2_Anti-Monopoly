package at.aau.anti_mon.client.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowMetrics;

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
        if (getActionBar() != null) {
            getActionBar().hide();
        }

        int width;
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            width = windowMetrics.getBounds().width();
            height = windowMetrics.getBounds().height();
        } else {
            // @deprecated used for older versions, but still needed for compatibility
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            width = dm.widthPixels;
            height = dm.heightPixels;
        }

        getWindow().setLayout((int)(width*.8),(int)(height*.8));
        processIntent();
    }

    public void onX(View view) {
        finish();
    }
    void processIntent() {
        Log.d(this.getClass().getName(), "in processIntent");
    }
}
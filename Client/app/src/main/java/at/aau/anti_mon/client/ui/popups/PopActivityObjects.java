package at.aau.anti_mon.client.ui.popups;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.ui.adapter.PropertyGameCardAdapter;

public class PopActivityObjects extends Activity {
    private int layout = R.layout.popup_objects;
    private static final float POPUP_SIZE = 0.65f;
    private User user;

    public PopActivityObjects() {
    }

    public PopActivityObjects(int layout, User user) {
        this.layout = layout;
        this.user = user;
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
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            width = dm.widthPixels;
            height = dm.heightPixels;
        }

        getWindow().setLayout((int) (width * POPUP_SIZE), (int) (height * POPUP_SIZE));
        processIntent();

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.property_cards_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PropertyGameCardAdapter adapter = new PropertyGameCardAdapter(user.getPropertyGameCards());
        recyclerView.setAdapter(adapter);
    }

    public void onX(View view) {
        finish();
    }

    void processIntent() {
        Log.d(this.getClass().getName(), "in processIntent");
    }
}
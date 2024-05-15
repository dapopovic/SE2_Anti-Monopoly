package at.aau.anti_mon.client.activities;

import android.util.Log;
import android.widget.TextView;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;

public class PopActivityRole extends PopActivityObjects {
    public PopActivityRole() {
        super(R.layout.activity_pop_roles);
    }

    @Override
    public void processIntent() {
        super.processIntent();
        // get role and username
        Roles role = Roles.valueOf(getIntent().getStringExtra("role"));
        String username = getIntent().getStringExtra("username");
        Figures figure = Figures.valueOf(getIntent().getStringExtra("figure"));
        TextView currentRole = findViewById(R.id.txtCurrentRole);
        String text = getResources().getString(R.string.your_role_is, role.toString(), username, figure);
        currentRole.setText(text);
    }
}
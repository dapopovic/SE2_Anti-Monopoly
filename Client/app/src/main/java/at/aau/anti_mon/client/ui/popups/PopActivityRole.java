package at.aau.anti_mon.client.ui.popups;

import android.widget.TextView;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;

public class PopActivityRole extends BasePopUp {
    public PopActivityRole() {
        super(R.layout.popup_roles);
    }

    @Override
    public void processIntent() {
        super.processIntent();
        // get role and username
        Roles role = Roles.valueOf(getIntent().getStringExtra("at.aau.anti_mon.client.role"));
        String username = getIntent().getStringExtra("at.aau.anti_mon.client.username");
        Figures figure = Figures.valueOf(getIntent().getStringExtra("at.aau.anti_mon.client.figure"));
        TextView currentRole = findViewById(R.id.txtCurrentRole);
        String text = getResources().getString(R.string.your_role_is, role.toString(), username, figure);
        currentRole.setText(text);
    }
}
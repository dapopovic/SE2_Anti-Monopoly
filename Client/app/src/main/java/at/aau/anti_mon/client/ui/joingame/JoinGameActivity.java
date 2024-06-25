package at.aau.anti_mon.client.ui.joingame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.library.baseAdapters.BR;

import javax.inject.Inject;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ActivityJoinGameBinding;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.utilities.PreferenceManager;
import at.aau.anti_mon.client.utilities.UserManager;

public class JoinGameActivity extends BaseActivity<ActivityJoinGameBinding,JoinGameViewModel> {

    EditText usernameEditText;
    EditText pinEditText;

    @Inject
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        usernameEditText = findViewById(R.id.editText_joinGame_Name);
        pinEditText = findViewById(R.id.editText_joinGame_Pin);

        if (PreferenceManager.getInstance().getCurrentUsername() != null) {
            usernameEditText.setText(PreferenceManager.getInstance().getCurrentUsername());
        }

    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_join_game;
    }

    @Override
    protected Class<JoinGameViewModel> getViewModelClass() {
        return JoinGameViewModel.class;
    }

    public void onJoinGameClicked(View view) {
        String username = usernameEditText.getText().toString();
        String pin = pinEditText.getText().toString();
        PreferenceManager.getInstance().setCurrentUsername(username);
        PreferenceManager.getInstance().setCurrentPIN(pin);

        // add Name to Websocket URI
        MessagingUtility.connectToServerWithUserID(username);
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
        }
        if (pin.isEmpty()) {
            pinEditText.setError("Pin is required");
        }

        MessagingUtility.createUserMessage(username,pin,Commands.JOIN_GAME).sendMessage();
        userManager.setAppUser(new User.UserBuilder(username, false ,false).lobbyPin(Integer.valueOf(pin)).build());

        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    public void onCancelJoinGame(View view) {
        // Go back to last Activity on Stack (StartMenuActivity)
        finish();
    }

}
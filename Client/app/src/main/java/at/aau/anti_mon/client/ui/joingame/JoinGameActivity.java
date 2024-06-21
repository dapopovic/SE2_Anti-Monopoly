package at.aau.anti_mon.client.ui.joingame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ActivityJoinGameBinding;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;

/**
 * Activity to join a game
 */
public class JoinGameActivity extends BaseActivity<ActivityJoinGameBinding, JoinGameViewModel> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewDataBinding = getViewDataBinding();
        viewModel = getViewModel();

        setupButtonListener();
        setupLiveDataObservers();

        if (viewModel.getPreferenceManager().getCurrentUsername() != null) {
                viewDataBinding.editTextJoinGameName.setText(viewModel.getPreferenceManager().getCurrentUsername());
        }
    }

    private void startLobbyActivity(String username, String pin) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("pin", pin);
        intent.putExtra("isOwner", false);
        intent.putExtra("isReady", false);
        startActivity(intent);
    }

    private void setupButtonListener() {
        viewDataBinding.join.setOnClickListener(v -> viewModel.onJoinGameClicked(getViewDataBinding()));
        viewDataBinding.back.setOnClickListener(v -> finish());
    }

    private void setupLiveDataObservers() {
        viewModel.getCanStartLobby().observe(this, canStartLobby -> {
            if (canStartLobby != null && canStartLobby) {
                checkIfUserCanJoinLobby();
            }
        });

        viewModel.getErrorLiveData().observe(this, errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        );
        viewModel.getInfoLiveData().observe(this, infoMessage -> {
                Toast.makeText(this, infoMessage, Toast.LENGTH_SHORT).show();
                checkIfUserCanJoinLobby();
        });
    }

    private void checkIfUserCanJoinLobby() {
        Boolean canStartLobby = viewModel.getCanStartLobby().getValue();
        String infoMessage = viewModel.getInfoLiveData().getValue();

        if (canStartLobby != null && canStartLobby && infoMessage != null && infoMessage.equals("Erfolgreich der Lobby beigetreten.")) {
            startLobbyActivity(viewModel.getUsername().getValue(), viewModel.getPin().getValue());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewDataBinding.editTextJoinGameName.setText(viewModel.getPreferenceManager().getCurrentUsername());
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.clearObservers(this);
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



}
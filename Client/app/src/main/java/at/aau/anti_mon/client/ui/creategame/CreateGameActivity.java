package at.aau.anti_mon.client.ui.creategame;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ActivityStartNewGameBinding;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;

/**
 * Activity to create a new game
 */
public class CreateGameActivity extends BaseActivity<ActivityStartNewGameBinding, CreateGameViewModel>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewDataBinding = getViewDataBinding();
        viewModel = getViewModel();

        setupButtonListener();
        setupLiveDataObservers();
    }

    private void startLobbyActivity(String username, String pin) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("pin", pin);
        intent.putExtra("isOwner", true);
        intent.putExtra("isReady", true);
        startActivity(intent);
    }

    private void setupButtonListener() {
        viewDataBinding.newGameCreate.setOnClickListener(v -> viewModel.onCreateGameClicked(getViewDataBinding()));
        viewDataBinding.newGameCancel.setOnClickListener(v -> finish());
    }

    private void setupLiveDataObservers() {
        viewModel.getPin().observe(this, receivedPin -> startLobbyActivity(viewModel.getUsername().getValue(), receivedPin));
        viewModel.getErrorLiveData().observe(this, errorMessage -> Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show());
        viewModel.getInfoLiveData().observe(this, infoMessage -> Toast.makeText(this, infoMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.resetPin();
        viewDataBinding.username.setText(viewModel.getPreferenceManager().getCurrentUsername());
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.resetPin();
        viewModel.clearObservers(this);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start_new_game;
    }

    @Override
    protected Class<CreateGameViewModel> getViewModelClass() {
        return CreateGameViewModel.class;
    }

}

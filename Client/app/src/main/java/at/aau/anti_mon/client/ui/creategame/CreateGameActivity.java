package at.aau.anti_mon.client.ui.creategame;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ActivityCreateGameBinding;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;

/**
 * Activity to create a new game
 */
public class CreateGameActivity extends BaseActivity<ActivityCreateGameBinding, CreateGameViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewDataBinding = getViewDataBinding();
        viewModel = getViewModel();

        setupButtonListener();
        setupLiveDataObservers();
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

    private void startLobbyActivity(String username, String pin) {
        userManager.createAppUser(username, Integer.valueOf(pin));
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
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
        return R.layout.activity_create_game;
    }

    @Override
    protected Class<CreateGameViewModel> getViewModelClass() {
        return CreateGameViewModel.class;
    }

}

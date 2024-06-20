package at.aau.anti_mon.client.ui.startmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ActivityStartPageBinding;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.instructions.GameInstructionsActivity;
import at.aau.anti_mon.client.ui.creategame.CreateGameActivity;
import at.aau.anti_mon.client.ui.joingame.JoinGameActivity;
import at.aau.anti_mon.client.ui.loadgame.LoadGameActivity;

public class StartMenuActivity extends BaseActivity<ActivityStartPageBinding, StartMenuViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStartGame(View view) {
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

    public void onJoinGame(View view) {
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }

    public void loadGame(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        startActivity(intent);
    }

    public void gameInstructions(View view) {
        Intent intent = new Intent(this, GameInstructionsActivity.class);
        startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start_page;
    }

    @Override
    protected Class<StartMenuViewModel> getViewModelClass() {
        return StartMenuViewModel.class;
    }

}


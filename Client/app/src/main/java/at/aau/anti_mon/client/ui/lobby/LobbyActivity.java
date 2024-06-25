package at.aau.anti_mon.client.ui.lobby;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ActivityLobbyBinding;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.ui.adapter.LobbyUserAdapter;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.gameboard.GameBoardActivity;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.utilities.MessagingUtility;

/**
 * LobbyActivity class to handle the lobby of the game
 */
public class LobbyActivity extends BaseActivity<ActivityLobbyBinding, LobbyViewModel> {

    private LobbyUserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRecyclerView();
        setupLiveDataObservers();
        setupButtonListener();
        if (viewModel.getUserManager() == null) {
            Log.e("LobbyActivity", "UserManager wurde nicht injiziert");
        }

        // Initialisiere AppUser
        userAdapter.addUser(userManager.getAppUser());
        viewModel.initAppUser();
    }

    private void setupRecyclerView(){
        userAdapter = new LobbyUserAdapter(getViewModel());
        RecyclerView recyclerView = viewDataBinding.recyclerViewUsers;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void setupButtonListener() {
        viewDataBinding.lobbyCancel.setOnClickListener(v -> viewModel.onLeaveButton(this));
        viewDataBinding.lobbyReady.setOnClickListener(v -> viewModel.onToggleReadyButton());
        viewDataBinding.lobbyStartGame.setOnClickListener(v -> viewModel.onStartGameButton());
    }

    private void setupLiveDataObservers() {
        viewModel.getUserListLiveData().observe(this, users -> {
            userAdapter.updateUsers(users);
            userManager.updateUsers(users);
            Log.d("LobbyActivity", "User list updated: " + users.size());
        });

        // Enables start game button if all users are ready
        viewModel.getStartGameEnabled().observe(this, enabled -> {
            viewDataBinding.lobbyStartGame.setEnabled(enabled);
            viewDataBinding.lobbyStartGame.setBackground(AppCompatResources.getDrawable(this, enabled ? R.drawable.rounded_btn : R.drawable.rounded_btn_disabled));
        });

        viewModel.getErrorLiveData().observe(this, errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        );
        viewModel.getInfoLiveData().observe(this, infoMessage ->
                Toast.makeText(this, infoMessage, Toast.LENGTH_SHORT).show()
        );

        // LiveData from StartNewGameCommand (Event) --> Starts Game
        viewModel.getStartGameLiveData().observe(this, this::startGame);

        viewModel.getAppUserLiveData().observe(this, user -> {
            if (user != null) {
                Log.d(DEBUG_TAG, "AppUserLiveData: " + user.getUserName());
                userAdapter.updateUser(user);

                if (user.isOwner()) {
                    viewDataBinding.lobbyStartGame.setVisibility(View.VISIBLE);
                } else {
                    viewDataBinding.lobbyStartGame.setVisibility(View.GONE);
                }

                if (user.isReady()) {
                    viewDataBinding.lobbyReady.setText(R.string.ready);
                } else {
                    viewDataBinding.lobbyReady.setText(R.string.unready);
                }
            }
        });
        viewModel.getUserJoinedLiveData().observe(this, user -> {
            userAdapter.addUser(user);
            userManager.addUser(user);
        });
        viewModel.getReadyUserEvent().observe(this, user -> userAdapter.updateUser(user));

        viewModel.getUserLeftLiveData().observe(this, user -> {
            userAdapter.removeUser(user);
            userManager.removeUser(user);
        });
    }


    private void startGame(Collection<User> users) {
        Toast.makeText(this, "Das Spiel wird gestartet.", Toast.LENGTH_SHORT).show();
        ArrayList<User> userList = new ArrayList<>(users);
        userManager.initializeUsers(userList);
        User appUser = users.stream().filter(user -> user.getUserName().equals(userManager.getAppUser().getUserName())).findFirst().orElse(null);
        userManager.setAppUser(appUser);
        users.forEach(user -> Log.d(DEBUG_TAG, "User: " + user.getUserName() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady() + " money: " + user.getPlayerMoney() + " role: " + user.getPlayerRole()));
        Intent intent = new Intent(this, GameBoardActivity.class);
        startActivity(intent);
    }

    /**
     * Heartbeat to keep connection alive
     * @param event HeartBeatEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onHeartBeatEvent(HeartBeatEvent event) {
        Log.d(DEBUG_TAG, "HeartBeatEvent");
        MessagingUtility.createHeartbeatMessage().sendMessage();
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
        return R.layout.activity_lobby;
    }

    @Override
    protected Class<LobbyViewModel> getViewModelClass() {
        return LobbyViewModel.class;
    }

}





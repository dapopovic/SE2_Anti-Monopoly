package at.aau.anti_mon.client.ui.lobby;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.adapters.LobbyUserAdapter;
import at.aau.anti_mon.client.databinding.ActivityLobbyBinding;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.gamefield.GameFieldActivity;

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
        processIntentAfterCreate();
        setupButtonListener();
    }

    private void setupRecyclerView(){
        userAdapter = new LobbyUserAdapter(getViewModel());
        RecyclerView recyclerView = viewDataBinding.recyclerViewUsers;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void setupButtonListener() {
        viewDataBinding.lobbyCancel.setOnClickListener(v -> viewModel.onLeaveButton(this));
        viewDataBinding.lobbyStartGame.setOnClickListener(v -> viewModel.onStartGameButton());
    }

    private void setupLiveDataObservers() {
        viewModel.getUserListLiveData().observe(this, users -> {
            userAdapter.updateUsers(users);
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
        //viewModel.getReadyUserEvent().observe(this, user -> userAdapter.updateUser(user));
    }

    /**
     * Process the intent after the activity has been created
     * and creates the user and pin for this lobby
     */
    private void processIntentAfterCreate() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        boolean isOwner = intent.getBooleanExtra("isOwner", false);
        boolean isReady = intent.getBooleanExtra("isReady", false);
        String pin = intent.getStringExtra("pin");
        User user = new User(username, isOwner, isReady);
        viewModel.initAppUser(user);
        viewModel.setAppUserPin(pin);
    }

    private void startGame(Collection<User> users) {
        Toast.makeText(this, "Das Spiel wird gestartet.", Toast.LENGTH_SHORT).show();
        users.forEach(user -> Log.d(DEBUG_TAG, "User: " + user.getUsername() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady() + " money: " + user.getMoney() + " role: " + user.getRole()));
        Intent intent = new Intent(this, GameFieldActivity.class);
        intent.putExtra("users", JsonDataManager.createJsonMessage(users));
        intent.putExtra("currentUser", JsonDataManager.createJsonMessage(viewModel.getAppUser()));
        intent.putExtra("pin", viewModel.getAppUserPin());
        startActivity(intent);
    }

    /**
     * Heartbeat to keep connection alive
     * @param event HeartBeatEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {
        Log.d(DEBUG_TAG, "HeartBeatEvent");
        MessagingService.createHeartbeatMessage().sendMessage();
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

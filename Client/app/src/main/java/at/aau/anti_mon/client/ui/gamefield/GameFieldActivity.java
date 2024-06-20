package at.aau.anti_mon.client.ui.gamefield;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.databinding.ActivityGamefieldBinding;
import at.aau.anti_mon.client.game.Player;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.popups.PopActivityHandel;
import at.aau.anti_mon.client.ui.popups.PopActivityObjects;
import at.aau.anti_mon.client.ui.popups.PopActivitySettings;
import at.aau.anti_mon.client.adapters.UserAdapter;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.events.ChangeBalanceEvent;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.networking.MessagingService;

/**
 * Activity für das Spielfeld des Anti-Monopoly-Spiels.
 */
public class GameFieldActivity extends BaseActivity<ActivityGamefieldBinding, GameFieldViewModel> {

    private UserAdapter userAdapter;
    //@Inject GameController gameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRecyclerView();
        setupLiveDataObservers();
        viewModel.processIntent(getIntent());
        setupButtonListener();
        viewModel.setUpResourceMap();
    }

    private void setupRecyclerView(){
        userAdapter = new UserAdapter(getViewModel());
        RecyclerView recyclerView = viewDataBinding.playersRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }


    private void setupButtonListener() {

        // Dice - Button
        viewDataBinding.rollDiceButton.setOnClickListener(v -> {
            Player currentPlayer = viewModel.getCurrentPlayer().getValue();
            if (currentPlayer != null && currentPlayer.isActive()) {
                int diceNumber = viewModel.rollDice();
                viewModel.moveCurrentPlayer(diceNumber);
            } else {
                Toast.makeText(this, "Nicht an der Reihe", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setupLiveDataObservers() {
        viewModel.getUserTurnData().observe(this, users -> {
            userAdapter.notifyDataSetChanged();
        });

        viewModel.getCurrentUser().observe(this, user -> {
            // Update UI with current user details
        });


        viewModel.getGameState().observe(this, gameState -> {
            switch (gameState) {
                case ROLL_DICE:
                    // UI für Würfeln vorbereiten
                    break;
                case MOVE_FIGURE:
                    // UI für Bewegung vorbereiten
                    break;
                case PLAYER_TURN:
                    // UI für Aktion vorbereiten
                    break;
                case NEXT_PLAYER:
                    // UI für den nächsten Spieler vorbereiten
                    break;
            }
        });

        viewModel.getGameLiveData().observe(this, game -> {
            // Update figure positions in the UI
            //updateFigurePositions(game.getPlayers());
        });

        viewModel.getFigurePositions().observe(this, positions -> {
            // Update figure positions in the UI
            updateFigurePositions(positions);
        });
    }

    private void setupFieldViews() {
        viewModel.setFieldView(0, findViewById(R.id.startField));
        viewModel.setFieldView(1, findViewById(R.id.rom1));
        viewModel.setFieldView(2, findViewById(R.id.rom2));
        viewModel.setFieldView(3, findViewById(R.id.rom3));
        viewModel.setFieldView(4, findViewById(R.id.rom4));
        viewModel.setFieldView(5, findViewById(R.id.rom5));
        viewModel.setFieldView(6, findViewById(R.id.rom6));
        viewModel.setFieldView(7, findViewById(R.id.rom7));
        viewModel.setFieldView(8, findViewById(R.id.rom8));
        viewModel.setFieldView(9, findViewById(R.id.rom9));
        viewModel.setFieldView(10, findViewById(R.id.sittingField));
        viewModel.setFieldView(11, findViewById(R.id.left1));
        viewModel.setFieldView(12, findViewById(R.id.left2));
        viewModel.setFieldView(13, findViewById(R.id.left_3));
        viewModel.setFieldView(14, findViewById(R.id.left_4));
        viewModel.setFieldView(15, findViewById(R.id.left_5));
        viewModel.setFieldView(16, findViewById(R.id.left_6));
        viewModel.setFieldView(17, findViewById(R.id.left_7));
        viewModel.setFieldView(18, findViewById(R.id.left_8));
        viewModel.setFieldView(19, findViewById(R.id.left_9));
        viewModel.setFieldView(20, findViewById(R.id.unluckyField));
        viewModel.setFieldView(21, findViewById(R.id.top_1));
        viewModel.setFieldView(22, findViewById(R.id.top_2));
        viewModel.setFieldView(23, findViewById(R.id.top_3));
        viewModel.setFieldView(24, findViewById(R.id.top_4));
        viewModel.setFieldView(25, findViewById(R.id.top_5));
        viewModel.setFieldView(26, findViewById(R.id.top_6));
        viewModel.setFieldView(27, findViewById(R.id.top_7));
        viewModel.setFieldView(28, findViewById(R.id.top_8));
        viewModel.setFieldView(29, findViewById(R.id.top_9));
        viewModel.setFieldView(30, findViewById(R.id.prison));
        viewModel.setFieldView(31, findViewById(R.id.right_1));
        viewModel.setFieldView(32, findViewById(R.id.right_2));
        viewModel.setFieldView(33, findViewById(R.id.right_3));
        viewModel.setFieldView(34, findViewById(R.id.right_4));
        viewModel.setFieldView(35, findViewById(R.id.right_5));
        viewModel.setFieldView(36, findViewById(R.id.right_6));
        viewModel.setFieldView(37, findViewById(R.id.right_7));
        viewModel.setFieldView(38, findViewById(R.id.right_8));
        viewModel.setFieldView(39, findViewById(R.id.right_9));
    }

    private void updateFigurePositions(int[] positions) {
        for (int i = 0; i < positions.length; i++) {
            int position = positions[i];
            View fieldView = viewModel.getFieldView(position);
            if (fieldView != null) {
                // Update die Position der Figur
                ImageView figure = findFigureViewById(i);
                if (figure != null) {
                    figure.setX(fieldView.getX());
                    figure.setY(fieldView.getY());
                }
            }
        }
    }

    private ImageView findFigureViewById(int figureId) {
        // Methode zur Suche der ImageView basierend auf figureId
        return null;
    }


    public void onSettings(View view) {
        startActivity(new Intent(getApplicationContext(), PopActivitySettings.class));
    }

    public void onHandel(View view) {
        startActivity(new Intent(getApplicationContext(), PopActivityHandel.class));
    }

    public void onObjects(View view) {
        startActivity(new Intent(getApplicationContext(), PopActivityObjects.class));
    }

    public int getID(String fieldId, String prefix) {
        String resourceName = (prefix != null) ? prefix + fieldId : fieldId;
        return getResources().getIdentifier(resourceName, "id", getPackageName());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {
        Log.d(DEBUG_TAG, "HeartBeatEvent");
        MessagingService.createHeartbeatMessage().sendMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDiceNumberReceivedEvent(DiceNumberReceivedEvent event) {
        if (event.getFigure() == null || event.getDicenumber() < 1 || event.getDicenumber() > 12) {
            Log.d("onDiceNumberReceivedEvent", "Invalid event data");
            return;
        }
        ImageView figure = findViewById(getID(event.getFigure(), null));
        if (figure != null) {
            viewModel.moveFigure(event.getUsername(), event.getLocation(), event.getDicenumber(), figure, this);
        } else {
            Log.e("onDiceNumberReceivedEvent", "Figure ImageView is null");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBalanceChangeReceivedEvent(ChangeBalanceEvent event) {
        userAdapter.updateUserMoney(event.getUsername(), event.getBalance());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d(DEBUG_TAG, "EventBus unregistered");
       // globalEventQueue.setEventBusReady(false);
        viewModel.clearObservers(this);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gamefield;
    }

    @Override
    protected Class<GameFieldViewModel> getViewModelClass() {
        return GameFieldViewModel.class;
    }



}


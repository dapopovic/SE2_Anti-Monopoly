package at.aau.anti_mon.client.ui.gameboard;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.databinding.ActivityGameboardBinding;
import at.aau.anti_mon.client.game.IUser;
import at.aau.anti_mon.client.game.PropertyGameCard;
import at.aau.anti_mon.client.game.PropertyGameCardInitializer;
import at.aau.anti_mon.client.game.UserProxy;
import at.aau.anti_mon.client.ui.base.BaseActivity;
import at.aau.anti_mon.client.ui.popups.PopActivityCheating;
import at.aau.anti_mon.client.ui.popups.PopActivityDice;
import at.aau.anti_mon.client.ui.popups.PopActivityHandel;
import at.aau.anti_mon.client.ui.popups.PopActivityObjects;
import at.aau.anti_mon.client.ui.popups.PopActivityRole;
import at.aau.anti_mon.client.ui.popups.PopActivitySettings;
import at.aau.anti_mon.client.ui.adapter.UserAdapter;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.events.EndGameEvent;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.events.WinGameEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.utilities.ResourceManager;
import at.aau.anti_mon.client.utilities.UserManager;

public class GameBoardActivity extends BaseActivity<ActivityGameboardBinding, GameBoardViewModel> {
    private static final int MAX_FIELD_COUNT = 40;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> diceActivityResultLauncher;
    private ActivityResultLauncher<Intent> settingsActivityResultLauncher;

    private ResourceManager resourceManager;
    private UserAdapter userAdapter;
    private GameDialogHelper gameDialogHelper;

    // SparseArrayCompat für das Mapping von View-IDs zu Bild- und Textressourcen
    private SparseArrayCompat<int[]> resourceMap;
    private SparseArrayCompat<Integer> fieldPositions;

    ArrayList<User> users;
    User appUser;

    IUser testProxyUser;

    String lobbyPin;
    private static final String COLOR_GRAY = "#6C757D";
    boolean showDialog = true;
    boolean surrender = false;

    @Inject GlobalEventQueue queue;
    @Inject UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRecyclerView();
        setupLiveDataObservers();

        queue.setEventBusReady(true);

        // Initialisiere das benutzerdefinierte GameBoardLayout
        GameBoardLayout gameBoardLayout = viewDataBinding.gameBoardLayout;
        gameBoardLayout.setViewModel(viewModel);

        testProxyUser = UserProxy.create(userManager);
        appUser = userManager.getAppUser();
        users = userManager.getUsersAsList();

        processIntent();
        //setUpResourceMap();
        sendFirst();
        setupResultLaunchers();

        // Initialisiere die PropertyGameCards
        SparseArrayCompat<PropertyGameCard> propertyGameCards = PropertyGameCardInitializer.initializePropertyGameCards();

        // Setze OnClickListener für die ImageViews im benutzerdefinierten Layout
        initializeClickListeners(gameBoardLayout, propertyGameCards);

        gameDialogHelper = new GameDialogHelper(this, appUser, viewModel);
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter();
        RecyclerView recyclerView = viewDataBinding.playersRecyclerView;//findViewById(R.id.players_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void initializeClickListeners(GameBoardLayout gameBoardLayout, SparseArrayCompat<PropertyGameCard> propertyGameCards) {
        for (int i = 0; i < propertyGameCards.size(); i++) {
            int key = propertyGameCards.keyAt(i);
            PropertyGameCard card = propertyGameCards.get(key);

            ImageView field = gameBoardLayout.findViewById(card.getId());   // id = fieldId
            if (field != null) {
                field.setOnClickListener(v -> {
                    Log.d("GameBoardActivity", card.getStreetName() + " clicked");
                    gameDialogHelper.showCustomDialog(this, card.getImageResId(),
                            card.getCityName() + " - " + card.getStreetName() +
                                    "\nPreis: " + card.getPrice(),card);
                });
            }
        }
    }

    private void sendFirst() {
        //ImageButton dice = findViewById(R.id.btndice);
        //dice.setEnabled(false);
        //dice.setBackgroundColor(Color.parseColor(COLOR_GRAY));
        //Button finish = findViewById(R.id.btnfinish);
        //finish.setEnabled(false);
        //finish.setBackgroundColor(Color.parseColor(COLOR_GRAY));
        MessagingUtility.createUserMessage(appUser.getUserName(), Commands.FIRST_PLAYER).sendMessage();
    }

    private void setupLiveDataObservers(){
        viewModel.getCurrentUser().observe(this, users -> {

        });
        viewModel.getInfoLiveData().observe(this, info -> {
            Log.d(DEBUG_TAG, "Info: " + info);
        });
        viewModel.getErrorLiveData().observe(this, error -> {
            Log.d(DEBUG_TAG, "Error: " + error);
        });

        viewModel.getLaunchDiceActivityEvent().observe(this, aVoid -> {
            Intent i = new Intent(getApplicationContext(), PopActivityDice.class);
            diceActivityResultLauncher.launch(i);
        });

        viewModel.getLaunchObjectsActivityEvent().observe(this, aVoid -> {
            Intent i = new Intent(getApplicationContext(), PopActivityObjects.class);
            startActivity(i);
        });

        viewModel.getLaunchHandelActivityEvent().observe(this, aVoid -> {
            Intent i = new Intent(getApplicationContext(), PopActivityHandel.class);
            startActivity(i);
        });

        viewModel.getLaunchSettingsActivityEvent().observe(this, aVoid -> {
            Intent i = new Intent(getApplicationContext(), PopActivitySettings.class);
            i.putExtra("at.aau.anti_mon.client.isOwner", appUser.isOwner());
            settingsActivityResultLauncher.launch(i);
        });

        viewModel.getLaunchRoleActivityEvent().observe(this, aVoid -> {
            Intent i = new Intent(getApplicationContext(), PopActivityRole.class);
            i.putExtra("at.aau.anti_mon.client.role", appUser.getPlayerRole().name());
            i.putExtra("at.aau.anti_mon.client.username", appUser.getUserName());
            i.putExtra("at.aau.anti_mon.client.figure", appUser.getPlayerFigure().name());
            startActivity(i);
        });

        viewModel.getFinishRoundEvent().observe(this, aVoid -> {
            viewModel.setDoubleDice(false);
            Button finish = findViewById(R.id.btnfinish);
            finish.setEnabled(false);
            finish.setBackgroundColor(Color.parseColor(COLOR_GRAY));
        });

        viewModel.getEnableFinishButtonLiveData().observe(this, enableFinish -> {
            Button finish = findViewById(R.id.btnfinish);
            finish.setEnabled(enableFinish);
            finish.setBackgroundColor(enableFinish ? Color.parseColor("#DC3545") : Color.parseColor(COLOR_GRAY));
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


        viewModel.getAppUsersTurn().observe(this, appUsersTurn -> {
            if (appUsersTurn) {
                ImageButton diceImageBtn = findViewById(R.id.btndice);
                diceImageBtn.setEnabled(true);
                diceImageBtn.setBackgroundColor(Color.parseColor("#28A745"));
            }else {
                ImageButton diceImageBtn = findViewById(R.id.btndice);
                diceImageBtn.setEnabled(false);
                diceImageBtn.setBackgroundColor(Color.parseColor(COLOR_GRAY));
            }
        });

        viewModel.getBalanceChangeEventData().observe(this, balanceChangeEvent -> {
            int newBalance = balanceChangeEvent.getBalance();
            String username = balanceChangeEvent.getUsername();
            Log.d(DEBUG_TAG, "Update Balance : "+ newBalance);
            userAdapter.updatePlayerMoney(username, newBalance);
        });

        viewModel.getNextPlayerLiveData().observe(this, nextPlayerEvent -> {
            String username = nextPlayerEvent.getUsername();
            userAdapter.updateCurrentPlayersTurn(username);
            if (Objects.equals(username, appUser.getUserName())) {
                viewModel.setAppUsersTurn(true);
            }
            Log.d(DEBUG_TAG, "NextPlayerEvent: " + username);
        });

        viewModel.getLooseGameEventData().observe(this, looseGameEvent -> {
            String username = looseGameEvent.getUsername();
            if(Objects.equals(username, appUser.getUserName())){
                makeDialog("Game over, you lost the game!");
            }else {
                userAdapter.updatePlayerLostTheGame(username);
            }
        });

        viewModel.getDiceNumberData().observe(this, diceNumberEvent -> {
            int diceNumber = diceNumberEvent.getDicenumber();
            String name = diceNumberEvent.getFigure();
            int location = diceNumberEvent.getLocation();
            if (name == null) {
                Log.d(DEBUG_TAG, "onDiceNumberReceivedEvent : name is null");
                return;
            }
            ImageView figure = findViewById(getID(name, null));
            moveFigure(location, diceNumber, figure);
            Log.d(DEBUG_TAG, "onDiceNumberReceivedEvent : " + name + " rolled " + diceNumber);
        });

        viewModel.getCheatingEventData().observe(this, cheatingEvent -> {
            Log.d(DEBUG_TAG, "Cheating event received!");
            Intent cheating = new Intent(this, PopActivityCheating.class);
            activityResultLauncher.launch(cheating);
        });

    }


    private void handleDiceActivityResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && data != null) {
            int firstNumber = data.getIntExtra("zahl1", 0);
            int secondNumber = data.getIntExtra("zahl2", 0);
            boolean dice = data.getBooleanExtra("Wurfel", false);

            if (dice) {
                if (Boolean.TRUE.equals(viewModel.getDoubleDiceLiveData().getValue()) && firstNumber == secondNumber) {
                    secondNumber = 0;
                }
                if (firstNumber == secondNumber) {
                    //doubledice = true;
                    viewModel.setDoubleDice(true);
                } else {
                    ImageButton diceImageBtn = findViewById(R.id.btndice);
                    diceImageBtn.setEnabled(false);
                    diceImageBtn.setBackgroundColor(Color.parseColor(COLOR_GRAY));
                    Button finish = findViewById(R.id.btnfinish);
                    finish.setEnabled(true);
                    finish.setBackgroundColor(Color.parseColor("#DC3545"));
                }
                viewModel.sendDice(firstNumber, secondNumber, false);

            }
        }
    }

    private void setupResultLaunchers() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleCheatActivityResult
        );
        diceActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleDiceActivityResult
        );
        settingsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleSettingsActivityResult
        );
    }


    private void handleSettingsActivityResult(ActivityResult result) {
        Intent data = result.getData();
        String setting = Objects.requireNonNull(data).getStringExtra("at.aau.anti_mon.client.setting");

        if(Objects.equals(setting, "endgame")){
            MessagingUtility.createUserMessage(appUser.getUserName(), Commands.END_GAME).sendMessage();
        }
        if (Objects.equals(setting, "surrender")){
            viewModel.onChangeBalanceEvent();
            viewModel.onLooseGameEvent();
            surrender = true;
        }
        if (Objects.equals(setting, "exitgame")){
            if(!surrender){
                viewModel.onChangeBalanceEvent();
                showDialog =false;
                viewModel.onLooseGameEvent();
            }
            finish();
        }
    }

    private void handleCheatActivityResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && data != null) {
            String resultData = data.getStringExtra("at.aau.anti_mon.client.resultKey");
            if (resultData != null && resultData.equals("yes")) {
                viewModel.sendDice(1, 0, true);
            }
        }
    }

    private void processIntent() {
        users = userManager.getUsersAsList();
        users.forEach(user -> Log.d(DEBUG_TAG, "User: " + user.getUserName() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady() + " money: " + user.getPlayerMoney()));
        //appUser = users.stream().filter(user -> user.getUserName().equals(appUser.getUserName())).findFirst().orElse(null);
        viewModel.setCurrentUser(appUser);
        userAdapter.updateData(users, appUser);
    }

    private void moveFigure(int location, int diceNumber, ImageView figure) {
        for (int i = 1; i <= diceNumber; i++) {
            if (location == MAX_FIELD_COUNT) location = 0;
            location++;
            Log.d(DEBUG_TAG, "move Figure to location: " + location);
            ImageView field = findViewById(getID(String.valueOf(location), "field"));
            figure.setX(field.getX());
            figure.setY(field.getY());
        }
    }

    public int getID(String fieldId, String prefix) {
        String resourceName = (prefix != null) ? prefix + fieldId : fieldId;
        try {
            Field field = R.id.class.getDeclaredField(resourceName);
            return field.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(DEBUG_TAG, "Field not found: " + resourceName);
            return -1; // Standard-ID oder Fehler-ID ?
        }
    }

    public void makeDialog(String message){
        if(showDialog){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Game ended!")
                    .setMessage(message)
                    .setPositiveButton("Keep watching?", (dialog, which) -> dialog.cancel())
                    .setNegativeButton("Exit Game", (dialog, which) -> finish())
                    .show();
        }
    }

    //public void setUpResourceMap(){
    //    resourceManager = new ResourceManager();
    //    resourceMap = resourceManager.getResourceMap();
    //    fieldPositions = resourceManager.getFieldPositions();
    //}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {
        Log.d(DEBUG_TAG, "HeartBeatEvent " + event.getHeartbeat());
        MessagingUtility.createHeartbeatMessage().sendMessage();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onWinGameEvent(WinGameEvent event) {
        if(Objects.equals(appUser.getUserName(), event.getUsername())){
            makeDialog("You are the winner!");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEndGameEvent(EndGameEvent event) {
        int rank = event.getRank();

        ImageButton diceImageBtn = findViewById(R.id.btndice);
        diceImageBtn.setEnabled(false);
        diceImageBtn.setBackgroundColor(Color.parseColor(COLOR_GRAY));
        Button finish = findViewById(R.id.btnfinish);
        finish.setEnabled(false);
        finish.setBackgroundColor(Color.parseColor(COLOR_GRAY));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game ended!")
                .setMessage("You are rank "+rank)
                .setPositiveButton("Keep watching?", (dialog, which) -> dialog.cancel())
                .setNegativeButton("Exit Game", (dialog, which) -> finish())
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        queue.setEventBusReady(true);
        Log.d(DEBUG_TAG, "GameBoardActivity resumed - EventBus registered");
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d(DEBUG_TAG, "GameBoardActivity stopped - EventBus unregistered");
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gameboard;
    }

    @Override
    protected Class<GameBoardViewModel> getViewModelClass() {
        return GameBoardViewModel.class;
    }
}
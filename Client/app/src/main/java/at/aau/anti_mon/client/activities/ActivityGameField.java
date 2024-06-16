package at.aau.anti_mon.client.activities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.adapters.UserAdapter;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.events.ChangeBalanceEvent;
import at.aau.anti_mon.client.events.CheatingEvent;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.events.NextPlayerEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;

public class ActivityGameField extends AppCompatActivity {
    private static final int MAX_FIELD_COUNT = 40;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> diceActivityResultLauncher;
    ArrayList<User> users;
    UserAdapter userAdapter;
    RecyclerView recyclerView;
    User currentUser;
    String pin;
    boolean doubledice = false;
    private static final String COLOR_GRAY = "#6C757D";
    private static final String USERNAME_STRING = "username";

    @Inject
    WebSocketClient webSocketClient;
    @Inject
    GlobalEventQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gamefield);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUI();
        processIntent();
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);

        sendFirst();
        initResultLaunchers();
    }

    private void initResultLaunchers() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleCheatActivityResult
        );
        diceActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleDiceActivityResult
        );
    }

    private void handleCheatActivityResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && data != null) {
            String resultData = data.getStringExtra("resultKey");
            if (resultData != null && resultData.equals("yes")) {
                sendDice(1, 0, true);
            }
        }
    }

    private void sendFirst() {
        ImageButton dice = findViewById(R.id.btndice);
        dice.setEnabled(false);
        dice.setBackgroundColor(Color.parseColor(COLOR_GRAY));
        Button finish = findViewById(R.id.btnfinish);
        finish.setEnabled(false);
        finish.setBackgroundColor(Color.parseColor(COLOR_GRAY));
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.FIRST_PLAYER, new HashMap<>());
        jsonDataDTO.putData(USERNAME_STRING, currentUser.getUsername());
        Log.d("onCreateGame", "Send name:" + currentUser.getUsername());
        webSocketClient.sendJsonData(jsonDataDTO);
    }

    private void initUI() {
        recyclerView = findViewById(R.id.players_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void processIntent() {
        Intent intent = getIntent();
        if (!intent.hasExtra("users") || !intent.hasExtra("currentUser") || !intent.hasExtra("pin")) {
            Log.e(DEBUG_TAG, "Intent is missing extras");
            finish();
            return;
        }
        users = new ArrayList<>();
        User[] usersList = JsonDataManager.parseJsonMessage(intent.getStringExtra("users"), User[].class);
        Collections.addAll(users, usersList);
        // with user.getMoney() I can get the balance of a user!
        users.forEach(user -> Log.d(DEBUG_TAG, "User: " + user.getUsername() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady() + " money: " + user.getMoney()));
        currentUser = JsonDataManager.parseJsonMessage(intent.getStringExtra("currentUser"), User.class);
        if (currentUser != null) {
            Log.d(DEBUG_TAG, "Current User: " + currentUser.getUsername() + " isOwner: " + currentUser.isOwner() + " isReady: " + currentUser.isReady() + " money: " + currentUser.getMoney());
        }
        currentUser = users.stream().filter(user -> user.getUsername().equals(currentUser.getUsername())).findFirst().orElse(null);
        userAdapter = new UserAdapter(users, currentUser);
        recyclerView.setAdapter(userAdapter);
        pin = intent.getStringExtra("pin");
        // show the current role of the user in a popup
        Intent i = new Intent(getApplicationContext(), PopActivityRole.class);
        i.putExtra("role", currentUser.getRole().name());
        i.putExtra(USERNAME_STRING, currentUser.getUsername());
        i.putExtra("figure", currentUser.getFigure().name());
        startActivity(i);
    }

    public void onSettings(View view) {
        Intent i = new Intent(getApplicationContext(), PopActivitySettings.class);
        startActivity(i);
    }

    public void onHandel(View view) {
        Intent i = new Intent(getApplicationContext(), PopActivityHandel.class);
        startActivity(i);
    }

    public void onObjects(View view) {
        Intent i = new Intent(getApplicationContext(), PopActivityObjects.class);
        startActivity(i);
    }

    public void onEndGame(View view) {
        doubledice = false;
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.NEXT_PLAYER, new HashMap<>());
        jsonDataDTO.putData(USERNAME_STRING, currentUser.getUsername());
        Log.d("onEndGame", "Send name:" + currentUser.getUsername());
        webSocketClient.sendJsonData(jsonDataDTO);
        Button finish = findViewById(R.id.btnfinish);
        finish.setEnabled(false);
        finish.setBackgroundColor(Color.parseColor(COLOR_GRAY));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        queue.setEventBusReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onFigureMove(View view) {
        Intent i = new Intent(getApplicationContext(), PopActivityDice.class);
        diceActivityResultLauncher.launch(i);
    }

    private void handleDiceActivityResult(ActivityResult result) {
        final String ON_ACTIVITY_RESULT = "onActivityResult";
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && data != null) {
            int firstNumber = data.getIntExtra("zahl1", 0);
            int secondNumber = data.getIntExtra("zahl2", 0);
            boolean dice = data.getBooleanExtra("Wurfel", false);

            // Process the received data
            Log.d(ON_ACTIVITY_RESULT, "zahl1: " + firstNumber);
            Log.d(ON_ACTIVITY_RESULT, "zahl2: " + secondNumber);
            Log.d(ON_ACTIVITY_RESULT, "wurfel: " + dice);

            if (dice) {
                if (doubledice && firstNumber == secondNumber) {
                    secondNumber = 0;
                }
                if (firstNumber == secondNumber) {
                    doubledice = true;
                } else {
                    ImageButton diceImageBtn = findViewById(R.id.btndice);
                    diceImageBtn.setEnabled(false);
                    diceImageBtn.setBackgroundColor(Color.parseColor(COLOR_GRAY));
                    Button finish = findViewById(R.id.btnfinish);
                    finish.setEnabled(true);
                    finish.setBackgroundColor(Color.parseColor("#DC3545"));
                }
                sendDice(firstNumber, secondNumber, false);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {
        Log.d("ANTI-MONOPOLY-DEBUG", "HeartBeatEvent");

        JsonDataDTO jsonData = new JsonDataDTO(Commands.HEARTBEAT, new HashMap<>());
        jsonData.putData("msg", "PONG");
        String jsonMessage = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDiceNumberReceivedEvent(DiceNumberReceivedEvent event) {
        int diceNumber = event.getDicenumber();
        String name = event.getFigure();
        int location = event.getLocation();
        if (name == null) {
            Log.d("onDiceNumberReceivedEvent", "name is null");
            return;
        }

        ImageView figure = findViewById(getID(name, null));
        moveFigure(location, diceNumber, figure);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBalanceChangeReceivedEvent(ChangeBalanceEvent event) {
        int newBalance = event.getBalance();
        String username = event.getUsername();
        Log.d("Update_balance", String.valueOf(newBalance));
        userAdapter.updateUserMoney(username, newBalance);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheatingReceivedEvent(CheatingEvent event) {
        Log.d("Cheating", "Cheating event received!");
        Intent cheating = new Intent(this, PopActivityCheating.class);
        activityResultLauncher.launch(cheating);
    }

    private void moveFigure(int location, int diceNumber, ImageView figure) {
        for (int i = 1; i <= diceNumber; i++) {
            if (location == MAX_FIELD_COUNT) location = 0;
            location++;
            Log.d("moveFigure", "location: " + location);
            ImageView field = findViewById(getID(String.valueOf(location), "field"));
            figure.setX(field.getX());
            figure.setY(field.getY());
        }
    }

    public int getID(String fieldId, String prefix) {
        String resourceName = (prefix != null) ? prefix + fieldId : fieldId;
        return getResources().getIdentifier(resourceName, "id", getPackageName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNextPlayerEvent(NextPlayerEvent event) {
        final String ON_NEXT_PLAYER = "onNextPlayerEvent";
        Log.d(ON_NEXT_PLAYER, "I am in onNextPlayerEvent");
        String username = event.getUsername();
        Log.d(ON_NEXT_PLAYER, "The next Player is: " + username);
        Log.d(ON_NEXT_PLAYER, "We are: " + currentUser.getUsername());

        userAdapter.currentPlayer(username);

        if (Objects.equals(username, currentUser.getUsername())) {
            Log.d(ON_NEXT_PLAYER, "We are in the if");
            ImageButton dice = findViewById(R.id.btndice);
            dice.setEnabled(true);
            dice.setBackgroundColor(Color.parseColor("#28A745"));
        }
    }

    public void sendDice(int dice1, int dice2, boolean cheat) {
        int dicenumber = dice1 + dice2;
        String dice = String.valueOf(dicenumber);
        String user = currentUser.getUsername();
        JsonDataDTO jsonData = new JsonDataDTO(Commands.DICENUMBER, new HashMap<>());
        jsonData.putData("dicenumber", dice);
        jsonData.putData(USERNAME_STRING, user);
        jsonData.putData("cheat", String.valueOf(cheat));
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.println(Log.DEBUG, "ActivityGameField", "Send dicenumber to server.");
    }
}
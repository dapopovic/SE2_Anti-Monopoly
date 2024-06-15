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
import java.util.Random;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.PopActivityDice;
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
    private static final int REQUEST_CODE_POP_ACTIVITY_DICE = 1;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Random random;
    ArrayList<User> users;
    UserAdapter userAdapter;
    RecyclerView recyclerView;
    User currentUser;
    String pin;
    boolean doubledice = false;
    String colorgrey = "#6C757D";
    String usernamestring = "username";

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
        random = new Random();
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);

        sendfirst();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String resultData = result.getData().getStringExtra("resultKey");
                        if (resultData != null && resultData.equals("yes")) {
                            sendDice(1, 0, true);
                        }
                    }
                }
        );

    }

    private void sendfirst(){
        ImageButton dice = findViewById(R.id.btndice);
        dice.setEnabled(false);
        dice.setBackgroundColor(Color.parseColor(colorgrey));
        Button finish = findViewById(R.id.btnfinish);
        finish.setEnabled(false);
        finish.setBackgroundColor(Color.parseColor(colorgrey));
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.FIRST_PLAYER, new HashMap<>());
        jsonDataDTO.putData(usernamestring, currentUser.getUsername());
        Log.d("onCreateGame", "Send name:"+currentUser.getUsername());
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
        i.putExtra(usernamestring, currentUser.getUsername());
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
        jsonDataDTO.putData(usernamestring, currentUser.getUsername());
        Log.d("onEndGame", "Send name:"+currentUser.getUsername());
        webSocketClient.sendJsonData(jsonDataDTO);
        Button finish = findViewById(R.id.btnfinish);
        finish.setEnabled(false);
        finish.setBackgroundColor(Color.parseColor(colorgrey));
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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onFigureMove(View view) {
        Intent i = new Intent(getApplicationContext(), PopActivityDice.class);
        //startActivity(i);
        startActivityForResult(i, REQUEST_CODE_POP_ACTIVITY_DICE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "I am in onActivityResult");
        if (requestCode == REQUEST_CODE_POP_ACTIVITY_DICE && resultCode == RESULT_OK) {
            if (data != null) {
                int number1 = data.getIntExtra("zahl1", 0);
                int number2 = data.getIntExtra("zahl2", 0);
                boolean wurfel = data.getBooleanExtra("Wurfel", false);

                // Verarbeite die empfangenen Daten
                Log.d("onActivityResult", "zahl1: " + number1);
                Log.d("onActivityResult", "zahl2: " + number2);
                Log.d("onActivityResult", "wurfel: " + wurfel);

                if (wurfel){
                    if(doubledice){
                        number2 = 0;
                    }
                    if(number1 == number2){
                        doubledice = true;
                    }
                    if(number1!=number2){
                        ImageButton dice = findViewById(R.id.btndice);
                        dice.setEnabled(false);
                        dice.setBackgroundColor(Color.parseColor(colorgrey));
                        Button finish = findViewById(R.id.btnfinish);
                        finish.setEnabled(true);
                        finish.setBackgroundColor(Color.parseColor("#DC3545"));
                    }
                    sendDice(number1,number2,false);
                }
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
        String username = event.getUsername();
        if (name == null) {
            Log.d("onDiceNumberReceivedEvent", "name is null");
            return;
        }
        if (diceNumber < 1 || diceNumber > 12) {
            Log.d("onDiceNumberReceivedEvent", "diceNumber is out of range, should be between 2 and 12");
            return;
        }

        ImageView figure = findViewById(getID(name, null));
        moveFigure(username, location, diceNumber, figure);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBalanceChangeReceivedEvent(ChangeBalanceEvent event) {
        int new_balance = event.getBalance();
        String username = event.getUsername();
        Log.d("Update_balance", String.valueOf(new_balance));
        userAdapter.updateUserMoney(username, new_balance);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheatingReceivedEvent(CheatingEvent event) {
        Log.d("Cheating", "Cheating event received!");
        Intent cheating = new Intent(this, PopActivityCheating.class);
        activityResultLauncher.launch(cheating);
    }

    private void moveFigure(String username, int location, int diceNumber, ImageView figure) {
        for (int i = 1; i <= diceNumber; i++) {
            if (location == MAX_FIELD_COUNT) {
                location = 0;
                // increase here the balance on bank account on 100 Euro
                if (username.equals(currentUser.getUsername())) {
                    int new_balance;
                    if (i == diceNumber) {
                        new_balance = currentUser.getMoney() + 200;
                    }
                    else {
                        new_balance = currentUser.getMoney() + 100;
                    }
                    JsonDataDTO jsonData = new JsonDataDTO(Commands.CHANGE_BALANCE, new HashMap<>());
                    jsonData.putData("new_balance", String.valueOf(new_balance));
                    jsonData.putData(usernamestring, currentUser.getUsername());
                    String jsonMessage = JsonDataManager.createJsonMessage(jsonData);
                    webSocketClient.sendMessageToServer(jsonMessage);
                }

            }
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
        Log.d("onNextPlayerEvent", "I am in onNextPlayerEvent");
        String username = event.getUsername();
        Log.d("onNextPlayerEvent", "The next Player is: "+username);
        Log.d("onNextPlayerEvent", "We are: "+currentUser.getUsername());

        userAdapter.currentPlayer(username);

        if(Objects.equals(username, currentUser.getUsername())){
            Log.d("onNextPlayerEvent", "We are in the if");
            ImageButton dice = findViewById(R.id.btndice);
            dice.setEnabled(true);
            dice.setBackgroundColor(Color.parseColor("#28A745"));
            //Button Finish = findViewById(R.id.btnFinish);
            //Finish.setEnabled(true);
        }
    }
    public void sendDice(int dice1, int dice2, boolean cheat){
        int dicenumber = dice1+dice2;
        String dice = String.valueOf(dicenumber);
        String user = currentUser.getUsername();
        JsonDataDTO jsonData = new JsonDataDTO(Commands.DICENUMBER, new HashMap<>());
        jsonData.putData("dicenumber", dice);
        jsonData.putData(usernamestring, user);
        jsonData.putData("cheat", String.valueOf(cheat));
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.println(Log.DEBUG, "ActivityGameField", "Send dicenumber to server.");
    }
}
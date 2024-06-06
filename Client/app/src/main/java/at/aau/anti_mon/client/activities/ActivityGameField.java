package at.aau.anti_mon.client.activities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SparseArrayCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.ArrayList;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.adapters.UserAdapter;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.game.GameController;
import at.aau.anti_mon.client.game.GameState;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.DiceNumberCommand;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.GameFieldViewModel;

/**
 * Activity für das Spielfeld des Anti-Monopoly-Spiels.
 * Zeigt das Spielfeld an und ermöglicht es dem Benutzer, auf die einzelnen Felder zu klicken, um Informationen zu erhalten.
 */
public class ActivityGameField extends AppCompatActivity {

    private GameFieldViewModel gameFieldViewModel;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private static final int MAX_FIELD_COUNT = 40;
    ArrayList<User> users;
    User currentUser;
    String pin;

    // SparseArrayCompat für das Mapping von View-IDs zu Bild- und Textressourcen
    private SparseArrayCompat<int[]> resourceMap;
    @Inject  GameController gameController;
    @Inject WebSocketClient webSocketClient;
    //@Inject JsonDataManager jsonDataManager;

    /*
    private final int[] imageViewIds = {
            R.id.field1,R.id.field2,R.id.field3,R.id.field4,R.id.field5,R.id.field6,R.id.field7,R.id.field8,R.id.field9,
            R.id.left_1,R.id.left_2,R.id.left_3,R.id.left_4,R.id.left_5,R.id.left_6,R.id.left_7,R.id.left_8,R.id.left_9,
            R.id.top_1,R.id.top_2,R.id.top_3,R.id.top_4,R.id.top_5,R.id.top_6,R.id.top_7,R.id.top_8,R.id.top_9,
            R.id.right_1,R.id.right_2,R.id.right_3,R.id.right_4,R.id.right_5,R.id.right_6,R.id.right_7,R.id.right_8,R.id.right_9
    };
     */

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

        gameFieldViewModel = new ViewModelProvider(this).get(GameFieldViewModel.class);
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(gameFieldViewModel);
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
        //((AntiMonopolyApplication) getApplication()).getAppComponent().inject(gameController);
        initUI();

        // Initialisiere das Mapping
        initResourceMap();

        processIntent();
        setupObservers();

        // Initialisiert alle ImageView-Elemente mit einen OnClickListener
        for (int i = 0; i < resourceMap.size(); i++) {
            int key = resourceMap.keyAt(i);
            ImageView imageView = findViewById(key);
            imageView.setOnClickListener(this::onImageViewClick);
        }

        initializeGame();
    }


    /**
     * Setzt die Observer für die LiveData-Objekte.
     */
    private void setupObservers() {
        gameFieldViewModel.getGameState().observe(this, gameState -> {
            switch (gameState) {

                //TODO: Implementiere die Logik für die verschiedenen Spielzustände
                case START_TURN:
                    // Start turn logic
                    break;

                // Player is in turn
                case THROW_DICE:
                    break;

                // Player is in turn
                case END_TURN:
                    break;

                // Player is in turn
                case WINNING:
                    break;

                // Player is in turn
                case LOOSING:
                    break;
            }
        });
    }


    private void onImageViewClick(View view) {
        int viewId = view.getId();
        int[] resources = resourceMap.get(viewId);

        if (resources != null) {
            //int imageResId = resources[0];
            //int textResId = resources[1];
            //String message = getString(textResId);

            // Zeige Dialog mit übergebenen Informationen
            showCustomDialog(this, resources[0], getString(resources[1]));
        }
    }

    private void showCustomDialog(Context context, int imageResId, String message) {
        // LayoutInflater um Layout zu laden
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom, null);

        // Bild und Text setzen
        ImageView imageView = view.findViewById(R.id.dialog_image);
        imageView.setImageResource(imageResId);

        TextView textView = view.findViewById(R.id.dialog_description);
        textView.setText(message); // Setze den Text dynamisch

        // AlertDialog erstellen
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Feldinformation")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Dialog anzeigen
        builder.create().show();
    }


    private void processIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("users") && intent.hasExtra("currentUser") && intent.hasExtra("pin")) {
            gameFieldViewModel.setUsers(JsonDataManager.parseJsonMessage(intent.getStringExtra("users"), User[].class));
            gameFieldViewModel.setCurrentUser(JsonDataManager.parseJsonMessage(intent.getStringExtra("currentUser"), User.class));
            gameFieldViewModel.setPin(intent.getStringExtra("pin"));
            userAdapter = new UserAdapter(gameFieldViewModel.getUsers(), gameFieldViewModel.getCurrentUser());
            recyclerView.setAdapter(userAdapter);
        } else {
            Log.e(DEBUG_TAG, "Intent is missing extras");
            finish();
        }
    }

    private void initUI() {
        recyclerView = findViewById(R.id.players_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeGame() {
        //Initialisiere GameState
        gameFieldViewModel.setGameState(GameState.START_TURN);

        gameController.initializeGame();
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

    public void onEndGame(View view) {
        gameFieldViewModel.leaveGame();
        finish();
    }

    private void initResourceMap() {
        resourceMap = new SparseArrayCompat<>();

        //Bottom ResourceMap
        resourceMap.put(R.id.field1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        resourceMap.put(R.id.field2, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.field3, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        resourceMap.put(R.id.field4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        resourceMap.put(R.id.field5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        resourceMap.put(R.id.field6, new int[]{R.drawable.berlin_1, R.string.berlin1_field_description});
        resourceMap.put(R.id.field7, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.field8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        resourceMap.put(R.id.field9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});

        //Left ResourceMap
        resourceMap.put(R.id.left_1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        resourceMap.put(R.id.left_2, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.left_3, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        resourceMap.put(R.id.left_4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        resourceMap.put(R.id.left_5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        resourceMap.put(R.id.left_7, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.left_8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        resourceMap.put(R.id.left_9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});

        //Top ResourceMap
        resourceMap.put(R.id.top_1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        resourceMap.put(R.id.top_2, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.top_3, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        resourceMap.put(R.id.top_4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        resourceMap.put(R.id.top_5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        resourceMap.put(R.id.top_7, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.top_8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        resourceMap.put(R.id.top_9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});

        //Right ResourceMap
        resourceMap.put(R.id.right_1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        resourceMap.put(R.id.right_2, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.right_3, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.right_4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        resourceMap.put(R.id.right_5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        resourceMap.put(R.id.right_6, new int[]{R.drawable.berlin_1, R.string.berlin1_field_description});
        resourceMap.put(R.id.right_7, new int[]{R.drawable.take, R.string.take_field_description});
        resourceMap.put(R.id.right_8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        resourceMap.put(R.id.right_9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});

    }


    @Override
    protected void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
        queue.setEventBusReady(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onFigureMove(View view) {

        String dice = "4";
        String user = "GreenTriangle";
        JsonDataDTO jsonData = new JsonDataDTO(Commands.DICENUMBER,new HashMap<>());
        jsonData.putData("dicenumber", dice);
        jsonData.putData("username", user);
        String jsonDataString = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonDataString);
        Log.println(Log.DEBUG,"ActivityGameField","Send dicenumber to server.");

        /*JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.DICENUMBER);
        jsonDataDTO.putData("dicenumber", "1");
        jsonDataDTO.putData("name", "GreenTriangle");
        queue.setEventBusReady(true);

        DiceNumberCommand diceNumberCommand = new DiceNumberCommand(queue);
        diceNumberCommand.execute(jsonDataDTO);*/
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeartBeatEvent(HeartBeatEvent event) {

        Log.d("ANTI-MONOPOLY-DEBUG", "HeartBeatEvent");


        JsonDataDTO jsonData = new JsonDataDTO(Commands.HEARTBEAT, new HashMap<>());
        jsonData.putData("msg", "PONG");
        String jsonMessage = JsonDataManager.createJsonMessage(jsonData);
        webSocketClient.sendMessageToServer(jsonMessage);
    }


    int greentriangleLocation = 1;
    int greensquareLocation = 1;
    int greencircleLocation = 1;

    int bluetriangleLocation = 1;
    int bluesquareLocation = 1;
    int bluecircleLocation = 1;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDiceNumberReceivedEvent(DiceNumberReceivedEvent event) {
        int diceNumber = event.getDicenumber();
        String name = event.getName();
        if (name == null) {
            Log.d("onDiceNumberReceivedEvent", "name is null");
            return;
        }
        if (diceNumber < 1 || diceNumber > 12) {
            Log.d("onDiceNumberReceivedEvent", "diceNumber is out of range, should be between 2 and 12");
            return;
        }
        if (name.equals("GreenTriangle")) {
            Log.d("onDiceNumberReceivedEvent", "name is here");
        }
        int location = switch (name) {
            case "GreenTriangle" -> greentriangleLocation = updateLocation(greentriangleLocation, diceNumber);
            case "GreenSquare" -> greensquareLocation = updateLocation(greensquareLocation, diceNumber);
            case "GreenCircle" -> greencircleLocation = updateLocation(greencircleLocation, diceNumber);
            case "BlueTriangle" -> bluetriangleLocation = updateLocation(bluetriangleLocation, diceNumber);
            case "BlueSquare" -> bluesquareLocation = updateLocation(bluesquareLocation, diceNumber);
            case "BlueCircle" -> bluecircleLocation = updateLocation(bluecircleLocation, diceNumber);
            default -> 1;
        };
        ImageView figure = findViewById(getID(name, null));
        moveFigure(location, diceNumber, figure);
    }
    private int updateLocation(int currentLocation, int diceNumber) {
        if (currentLocation + diceNumber > MAX_FIELD_COUNT) {
            return 1;
        }
        return currentLocation + diceNumber;
    }

    private void moveFigure(int location, int diceNumber, ImageView figure) {
        int goal = location + diceNumber;
        while (location < goal) {
            ImageView field = findViewById(getID(String.valueOf(location), "field"));
            location++;
            figure.setX(field.getX());
            figure.setY(field.getY());
        }
    }

    public int getID(String fieldId, String prefix) {
        String resourceName = (prefix != null) ? prefix + fieldId : fieldId;
        return getResources().getIdentifier(resourceName, "id", getPackageName());
    }
}


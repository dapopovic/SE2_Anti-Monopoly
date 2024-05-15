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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SparseArrayCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.adapters.UserAdapter;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;

public class ActivityGameField extends AppCompatActivity {
    ArrayList<User> users;
    UserAdapter userAdapter;
    RecyclerView recyclerView;
    User currentUser;
    String pin;

    @Inject
    WebSocketClient webSocketClient;

    // SparseArrayCompat für das Mapping von View-IDs zu Bild- und Textressourcen
    private SparseArrayCompat<int[]> resourceMap;

    /*
    private final int[] imageViewIds = {
            R.id.field1,R.id.field2,R.id.field3,R.id.field4,R.id.field5,R.id.field6,R.id.field7,R.id.field8,R.id.field9,
            R.id.left_1,R.id.left_2,R.id.left_3,R.id.left_4,R.id.left_5,R.id.left_6,R.id.left_7,R.id.left_8,R.id.left_9,
            R.id.top_1,R.id.top_2,R.id.top_3,R.id.top_4,R.id.top_5,R.id.top_6,R.id.top_7,R.id.top_8,R.id.top_9,
            R.id.right_1,R.id.right_2,R.id.right_3,R.id.right_4,R.id.right_5,R.id.right_6,R.id.right_7,R.id.right_8,R.id.right_9
    };
     */


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

        // Initialisiere das Mapping
        initResourceMap();

        processIntent();

        // Initialisiert alle ImageView-Elemente mit einen OnClickListener
        for (int i = 0; i < resourceMap.size(); i++) {
            int key = resourceMap.keyAt(i);
            ImageView imageView = findViewById(key);
            imageView.setOnClickListener(this::onImageViewClick);
        }

        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
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

    private void onImageViewClick(View view) {
        int viewId = view.getId();
        int[] resources = resourceMap.get(viewId);

        if (resources != null) {
            int imageResId = resources[0];
            int textResId = resources[1];
            String message = getString(textResId);

            // Zeige Dialog mit Informationen
            showCustomDialog(this, imageResId, message);
        }
    }

    private void showCustomDialog(Context context, int imageResId, String message) {
        // LayoutInflater um Layout zu laden
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom, null);

        // Bild und Text setzen
        ImageView imageView = view.findViewById(R.id.dialog_image);
        imageView.setImageResource(imageResId); // Setze das Bild dynamisch

        TextView textView = view.findViewById(R.id.dialog_text);
        textView.setText(message); // Setze den Text dynamisch

        // AlertDialog erstellen
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Feldinformation")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Dialog anzeigen
        builder.create().show();
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
        users.forEach(user -> Log.d(DEBUG_TAG, "User: " + user.getUsername() + " isOwner: " + user.isOwner() + " isReady: " + user.isReady() + " money: " + user.getMoney()));
        currentUser = JsonDataManager.parseJsonMessage(intent.getStringExtra("currentUser"), User.class);
        userAdapter = new UserAdapter(users, currentUser);
        recyclerView.setAdapter(userAdapter);
        if (currentUser != null) {
            Log.d(DEBUG_TAG, "Current User: " + currentUser.getUsername() + " isOwner: " + currentUser.isOwner() + " isReady: " + currentUser.isReady() + " money: " + currentUser.getMoney());
        }
        pin = intent.getStringExtra("pin");
    }

    public void onSettings(View view) {
        Intent i = new Intent(getApplicationContext(),PopActivitySettings.class);
        startActivity(i);
    }

    public void onHandel(View view) {
        Intent i = new Intent(getApplicationContext(),PopActivityHandel.class);
        startActivity(i);
    }

    public void onObjects(View view) {
        Intent i = new Intent(getApplicationContext(),PopActivityObjects.class);
        startActivity(i);
    }

    public void onEndGame(View view) {
        // only for now, because the server does not support END_GAME yet
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.LEAVE_GAME, new HashMap<>());
        jsonDataDTO.putData("username", currentUser.getUsername());
        jsonDataDTO.putData("pin", pin);
        webSocketClient.sendJsonData(jsonDataDTO);
        finish();
    }


}


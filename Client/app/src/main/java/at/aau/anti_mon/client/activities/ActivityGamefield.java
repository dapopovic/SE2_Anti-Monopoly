package at.aau.anti_mon.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.DiceNumberCommand;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;

public class ActivityGamefield extends AppCompatActivity {
    private static final int MAX_FIELD_COUNT = 40;

    @Inject
    GlobalEventQueue queue;
    @Inject
    WebSocketClient webSocketClient;

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
        ((AntiMonopolyApplication) getApplication()).getAppComponent().inject(this);
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

    public void onFinish(View view) {
        Intent i = new Intent(getApplicationContext(), StartMenuActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onFigureMove(View view) {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.DICENUMBER);
        jsonDataDTO.putData("dicenumber", "1");
        jsonDataDTO.putData("name", "GreenTriangle");
        queue.setEventBusReady(true);

        DiceNumberCommand diceNumberCommand = new DiceNumberCommand(queue);
        diceNumberCommand.execute(jsonDataDTO);
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
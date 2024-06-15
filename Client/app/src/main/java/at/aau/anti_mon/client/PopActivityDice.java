package at.aau.anti_mon.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class PopActivityDice extends Activity implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 1.5f;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private long mShakeTimestamp;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    ImageView dice1;
    ImageView dice2;
    TextView touchorshake;
    Random random = new Random();

    int number1 = 0;
    int number2 = 0;
    boolean rolldice = true;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_dice);
        try {
            getActionBar().hide();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), (int)(height * .8));

        rolldice = true;
        dice1 = findViewById(R.id.dice1);
        dice2 = findViewById(R.id.dice2);
        dice1.setEnabled(true);
        dice2.setEnabled(true);
        touchorshake = findViewById(R.id.txtTouchorShake);
        touchorshake.setVisibility(View.VISIBLE);


        dice1.setOnClickListener(v -> {
            rolldice = false;
            number1 = rolltheDice(dice1);
            number2 = rolltheDice(dice2);
            dice1.setEnabled(false);
            dice2.setEnabled(false);
            touchorshake.setVisibility(View.INVISIBLE);
        });

        dice2.setOnClickListener(v -> {
            rolldice = false;
            number1 = rolltheDice(dice1);
            number2 = rolltheDice(dice2);
            dice1.setEnabled(false);
            dice2.setEnabled(false);
            touchorshake.setVisibility(View.INVISIBLE);
        });

        touchorshake.setOnClickListener(v -> {
            rolldice = false;
            number1 = rolltheDice(dice1);
            number2 = rolltheDice(dice2);
            dice1.setEnabled(false);
            dice2.setEnabled(false);
            touchorshake.setVisibility(View.INVISIBLE);
        });

        // SensorManager und Beschleunigungssensor initialisieren
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Gerät hat keinen Beschleunigungssensor
            Log.e("PopActivityDice", "No accelerometer found!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && rolldice) {
            handleShakeEvent(event.values);
        }
    }

    private void handleShakeEvent(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        Log.d("SHAKE_EVENT", "gForce: " + gForce);  // Log-Ausgabe zur Fehlerbehebung

        if (gForce > SHAKE_THRESHOLD) {
            final long now = System.currentTimeMillis();
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }
            mShakeTimestamp = now;

            Log.d("SHAKE_EVENT", "Shake detected!");  // Log-Ausgabe zur Fehlerbehebung
            rolldice = false;
            dice1.setEnabled(false);
            dice2.setEnabled(false);
            touchorshake.setVisibility(View.INVISIBLE);

            number1 = rolltheDice(dice1);
            number2 = rolltheDice(dice2);
        }
    }
    private int rolltheDice(ImageView dice){
        int number = random.nextInt(6) + 1;
        Log.i("ROLLING", String.valueOf(number));
        switch (number) {
            case 1:
                dice.setImageResource(R.drawable.dice1);
                break;
            case 2:
                dice.setImageResource(R.drawable.dice2);
                break;
            case 3:
                dice.setImageResource(R.drawable.dice3);
                break;
            case 4:
                dice.setImageResource(R.drawable.dice4);
                break;
            case 5:
                dice.setImageResource(R.drawable.dice5);
                break;
            case 6:
                dice.setImageResource(R.drawable.dice6);
                break;
            default:
                break;
        }
        return number;
    }

    public void onX(View view) {
        Log.d("onX", "I am in onX from PopActivityDice");
        Intent resultIntent = new Intent();

        if (number1 != 0 && number2 != 0) {
            resultIntent.putExtra("zahl1", number1);
            resultIntent.putExtra("zahl2", number2);
            resultIntent.putExtra("Wurfel", true);
        } else {
            resultIntent.putExtra("Wurfel", false);
        }

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ignorieren für diesen Fall
    }
}

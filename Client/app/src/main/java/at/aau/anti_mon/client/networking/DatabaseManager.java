package at.aau.anti_mon.client.networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PLAYERS = "players";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MONEY = "money";

    public static final String TABLE_PROPERTIES = "properties";
    public static final String COLUMN_PROPERTY_ID = "property_id";
    public static final String COLUMN_PLAYER_OWNER_ID = "player_owner_id";
    public static final String COLUMN_PROPERTY_NAME = "property_name";
    public static final String COLUMN_PROPERTY_PRICE = "property_price";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPlayersTable = "CREATE TABLE " + TABLE_PLAYERS + " (" +
                COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_MONEY + " INTEGER" +
                ");";

        String createPropertiesTable = "CREATE TABLE " + TABLE_PROPERTIES + " (" +
                COLUMN_PROPERTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYER_OWNER_ID + " INTEGER, " +
                COLUMN_PROPERTY_NAME + " TEXT, " +
                COLUMN_PROPERTY_PRICE + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_PLAYER_OWNER_ID + ") REFERENCES " + TABLE_PLAYERS + "(" + COLUMN_PLAYER_ID + ")" +
                ");";

        db.execSQL(createPlayersTable);
        db.execSQL(createPropertiesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        onCreate(db);
    }

    public void addPlayer(String name, int money) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MONEY, money);
        db.insert(TABLE_PLAYERS, null, values);
        db.close();
    }

    public void addProperty(int playerId, String propertyName, int propertyPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_OWNER_ID, playerId);
        values.put(COLUMN_PROPERTY_NAME, propertyName);
        values.put(COLUMN_PROPERTY_PRICE, propertyPrice);
        db.insert(TABLE_PROPERTIES, null, values);
        db.close();
    }

    public List<String> getPlayerProperties(int playerId) {
        List<String> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROPERTIES, new String[]{COLUMN_PROPERTY_NAME},
                COLUMN_PLAYER_OWNER_ID + "=?", new String[]{String.valueOf(playerId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                properties.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return properties;
    }


    public String exportDatabaseToJson() {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject databaseJson = new JSONObject();

        try {
            // Export players table
            JSONArray playersArray = new JSONArray();
            Cursor playersCursor = db.query(TABLE_PLAYERS, null, null, null, null, null, null);
            if (playersCursor.moveToFirst()) {
                int playerIdIndex = playersCursor.getColumnIndex(COLUMN_PLAYER_ID);
                int nameIndex = playersCursor.getColumnIndex(COLUMN_NAME);
                int moneyIndex = playersCursor.getColumnIndex(COLUMN_MONEY);

                do {
                    JSONObject playerJson = new JSONObject();
                    playerJson.put(COLUMN_PLAYER_ID, playersCursor.getInt(playerIdIndex));
                    playerJson.put(COLUMN_NAME, playersCursor.getString(nameIndex));
                    playerJson.put(COLUMN_MONEY, playersCursor.getInt(moneyIndex));
                    playersArray.put(playerJson);
                } while (playersCursor.moveToNext());
            }
            playersCursor.close();
            databaseJson.put("players", playersArray);

            // Export properties table
            JSONArray propertiesArray = new JSONArray();
            Cursor propertiesCursor = db.query(TABLE_PROPERTIES, null, null, null, null, null, null);
            if (propertiesCursor.moveToFirst()) {
                int propertyIdIndex = propertiesCursor.getColumnIndex(COLUMN_PROPERTY_ID);
                int playerOwnerIdIndex = propertiesCursor.getColumnIndex(COLUMN_PLAYER_OWNER_ID);
                int propertyNameIndex = propertiesCursor.getColumnIndex(COLUMN_PROPERTY_NAME);
                int propertyPriceIndex = propertiesCursor.getColumnIndex(COLUMN_PROPERTY_PRICE);

                do {
                    JSONObject propertyJson = new JSONObject();
                    propertyJson.put(COLUMN_PROPERTY_ID, propertiesCursor.getInt(propertyIdIndex));
                    propertyJson.put(COLUMN_PLAYER_OWNER_ID, propertiesCursor.getInt(playerOwnerIdIndex));
                    propertyJson.put(COLUMN_PROPERTY_NAME, propertiesCursor.getString(propertyNameIndex));
                    propertyJson.put(COLUMN_PROPERTY_PRICE, propertiesCursor.getInt(propertyPriceIndex));
                    propertiesArray.put(propertyJson);
                } while (propertiesCursor.moveToNext());
            }
            propertiesCursor.close();
            databaseJson.put("properties", propertiesArray);

        } catch (JSONException e) {
            Log.d("DatabaseManager", "Error exporting database to JSON", e);
        }

        return databaseJson.toString();
    }

    public void syncDatabase(String databaseJson) {
        APIService apiService = APIClient.getApiService();
        Call<String> call = apiService.syncDatabase(databaseJson);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Database synced successfully");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("API", "Error syncing database", t);
            }
        });
    }
}
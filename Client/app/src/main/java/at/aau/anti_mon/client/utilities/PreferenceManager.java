package at.aau.anti_mon.client.utilities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import at.aau.anti_mon.client.game.User;

public class PreferenceManager {
    private static final String PREFERENCE_NAME = "my_prefs";
    private static final String PREFERENCE_USERNAME_KEY = "username";
    private static final String PREFERENCE_PIN_KEY = "pin";
    private static final String PREFERENCE_USER_KEY = "appUser";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static PreferenceManager instance;

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PreferenceManager.class) {
                if (instance == null) {
                    instance = new PreferenceManager(context);
                }
            }
        }
        return instance;
    }

    public static synchronized PreferenceManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PreferenceManager is not initialized!");
        }
        return instance;
    }

    public void setCurrentUsername(String username) {
        sharedPreferences.edit().putString(PREFERENCE_USERNAME_KEY, username).apply();
    }

    public String getCurrentUsername() {
        return sharedPreferences.getString(PREFERENCE_USERNAME_KEY, null);
    }

    public void setCurrentPIN(String pin) {
        editor.putString(PREFERENCE_PIN_KEY, pin).apply();
    }

    public String getCurrentPIN() {
        String pin =  sharedPreferences.getString(PREFERENCE_PIN_KEY, null);
        if (pin == null) {
            Log.e(DEBUG_TAG, "No Pin found in SharedPreferences");
            return null;
        }else{
            Log.i(DEBUG_TAG, "Current User: " + pin);
            return pin;
        }
    }

    public void setAppUser(User appUser){
        editor.putString(PREFERENCE_USER_KEY, JsonDataUtility.createJsonMessage(appUser)).apply();
        Log.i(DEBUG_TAG, "User saved in SharedPreferences: " + appUser.getUserName() + " isOwner: " + appUser.isOwner() + " isReady: " + appUser.isReady() + " money: " + appUser.getPlayerMoney());
    }

    public User getAppUser(){
        User appUser = JsonDataUtility.parseJsonMessage(sharedPreferences.getString(PREFERENCE_USER_KEY, null), User.class);
        if (appUser == null) {
            Log.e(DEBUG_TAG, "No User found in SharedPreferences");
            return null;
        }else{
            Log.i(DEBUG_TAG, "Current User: " + appUser.getUserName() + " isOwner: " + appUser.isOwner() + " isReady: " + appUser.isReady() + " money: " + appUser.getPlayerMoney());
            return appUser;
        }
    }


    // Methode zum Löschen aller gespeicherten Daten
    public void clearPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}

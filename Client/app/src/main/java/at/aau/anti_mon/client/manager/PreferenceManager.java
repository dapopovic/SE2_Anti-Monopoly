package at.aau.anti_mon.client.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREFERENCE_NAME = "my_prefs";
    private static final String PREFERENCE_USERNAME_KEY = "username";
    private static final String PREFERENCE_PIN_KEY = "pin";
    private final SharedPreferences sharedPreferences;
    private static PreferenceManager instance;

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
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

    //@Nullable
    public String getCurrentUsername() {
        return sharedPreferences.getString(PREFERENCE_USERNAME_KEY, null);
    }

    public void setCurrentPIN(String pin) {
        sharedPreferences.edit().putString(PREFERENCE_PIN_KEY, pin).apply();
    }

    //@Nullable
    public String getCurrentPIN() {
        return sharedPreferences.getString(PREFERENCE_PIN_KEY, null);
    }

    // Methode zum Löschen aller gespeicherten Daten
    public void clearPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}

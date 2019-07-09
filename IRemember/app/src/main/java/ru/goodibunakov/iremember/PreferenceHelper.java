package ru.goodibunakov.iremember;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GooDi on 16.09.2017.
 */

class PreferenceHelper {

    private static PreferenceHelper instance;
    static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";
//    private Context context;
    private SharedPreferences preferences;

    private PreferenceHelper() {
    }

    static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    void init(Context context) {
//        this.context = context;
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    Boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
}

package com.andrutyk.beeper.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Created by admin on 28.07.2016.
 */
public class PrefUtils {

    private final Context context;

    private final SharedPreferences prefs;
    private Editor editor;

    public static final String PREF_SOUND_NAME = "listSounds";
    public static final String PREF_COUNT_BEEP = "countBeep";
    public static final String PREF_TIME_TO_BEEP = "timeToBeep";

    /**
     * Manages everything relative to preferences
     *
     * @param context
     */
    public PrefUtils(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    Editor editor() {
        if (editor == null) {
            editor = prefs.edit();
        }
        return editor;
    }

    public SharedPreferences prefs() {
        return prefs;
    }

    /**
     * Put a String
     *
     * @param key
     *            the key of the preference
     * @param value
     *            the value string
     */
    public Editor putString(String key, String value) {
        final Editor editor = editor();
        editor.putString(key, value);
        return editor;
    }

    /**
     * After applying, call {@link #editor()} again.
     */
    public void apply() {
        apply(editor());
        editor = null;
    }

    @SuppressLint("NewApi")
    public static void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    public String getCurrSoundName(String defValue) {
        return prefs.getString(PREF_SOUND_NAME, defValue);
    }

    public int getCurrCountBeep(int defValue) {
        return prefs.getInt(PREF_COUNT_BEEP, defValue);
    }

    public int getCurrTimeToBeep(int defValue) {
        return prefs.getInt(PREF_TIME_TO_BEEP, defValue);
    }
}

package com.pccw.nowplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pccw.nowplayer.PlayerApplication;

/**
 * Created by Swifty on 3/23/2016.
 */
public class Pref {
    private static final String APP_SHARED_PREFS = "pref";
    /**
     * New Layout Params Key
     */

    private static Pref pref;

    private SharedPreferences sharedPreferences;

    public Pref(Context context, String name) {
        this.sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * Returns {@link Pref} instance
     *
     * @return instance of preference to use
     */
    public static synchronized Pref getPref() {
        if (null == pref)
            pref = new Pref(PlayerApplication.getContext(), APP_SHARED_PREFS);
        return pref;
    }

    public static synchronized Pref getPref(String name) {
        return new Pref(PlayerApplication.getContext(), name);
    }

    /**
     * Returns {@link Pref} instance to be used by this {@link SharedPreferences}
     *
     * @return instace of this applications shared preference
     */
    public static synchronized SharedPreferences getPreference() {
        return getPref().getSharedPreference();
    }

    public boolean getBool(String key) {
        return getBool(key, false);
    }

    public boolean getBool(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    /**
     * Returns an {@link SharedPreferences.Editor} attached to this pref
     *
     * @return preference editor
     */
    public SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public float getFloat(String key, float def) {
        return sharedPreferences.getFloat(key, def);
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }


    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }


    public SharedPreferences getSharedPreference() {
        return sharedPreferences;
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public String getString(String key, String def) {
        return sharedPreferences.getString(key, def);
    }

    public void putBool(String key, boolean v) {
        sharedPreferences.edit().putBoolean(key, v).apply();
    }

    public void putBoolSync(String key, boolean v) {
        sharedPreferences.edit().putBoolean(key, v).commit();
    }

    public void putFloat(String key, float v) {
        sharedPreferences.edit().putFloat(key, v).apply();
    }

    public void putFloatSync(String key, float v) {
        sharedPreferences.edit().putFloat(key, v).commit();
    }

    public void putInt(String key, int v) {
        sharedPreferences.edit().putInt(key, v).apply();
    }

    public void putIntSync(String key, int v) {
        sharedPreferences.edit().putInt(key, v).commit();
    }

    public void putLong(String key, long v) {
        sharedPreferences.edit().putLong(key, v).apply();
    }

    public void putLongSync(String key, long v) {
        sharedPreferences.edit().putLong(key, v).commit();
    }

    public void putString(String key, String v) {
        sharedPreferences.edit().putString(key, v).apply();
    }

    public void putStringSync(String key, String v) {
        sharedPreferences.edit().putString(key, v).commit();
    }
}
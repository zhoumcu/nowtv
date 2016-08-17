package com.pccw.nowplayer.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by Swifty on 5/3/2016.
 */
public class LocaleUtils {
    public enum Language {
        CHINESE,
        ENGLISH,
    }

    private static final String BROADCAST_STRING = "com.pccwnow.player.language";

    public static Language getCurrentLocale(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.toString().contains("zh") ? Language.CHINESE : Language.ENGLISH;
    }

    public static void changeLanguage(Activity activity, boolean isEnglish, Class<? extends Activity> clazz) {


        Intent i = new Intent(BROADCAST_STRING);
        activity.sendBroadcast(i);
    }

    public static void registerLanguageBroadcast(final Activity activity, BroadcastReceiver languageReceiver) {
        IntentFilter intentFilter = new IntentFilter(BROADCAST_STRING);
        activity.registerReceiver(languageReceiver, intentFilter);
    }


}

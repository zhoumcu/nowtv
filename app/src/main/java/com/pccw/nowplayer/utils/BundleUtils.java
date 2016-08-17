package com.pccw.nowplayer.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public class BundleUtils {
    private static final ObjectRefStore sharedData = new ObjectRefStore();


    @SuppressWarnings("unchecked")
    public static <T> T popData(Activity activity, String extraKey, Class<T> cls) {
        if (activity == null) return null;

        Intent intent = activity.getIntent();
        if (intent == null) return null;

        String objectKey = intent.getStringExtra(extraKey);
        Object ret = popData(objectKey);

        if (!cls.isInstance(ret)) return null;
        return (T) ret;
    }


    @SuppressWarnings("unchecked")
    public static <T> T popData(Fragment fragment, String argumentKey, Class<T> cls) {
        if (fragment == null) return null;

        Bundle extras = fragment.getArguments();
        if (extras == null) return null;

        String objectKey = extras.getString(argumentKey);
        Object ret = popData(objectKey);

        if (!cls.isInstance(ret)) return null;
        return (T) ret;
    }


    public static Object popData(String objectKey) {
        return sharedData.pop(objectKey);
    }


    public static String pushData(Object data) {
        return sharedData.put(data, true);
    }

    public static void pushData(Bundle bundle, String key, Object data) {
        if (bundle != null) {
            bundle.putString(key, pushData(data));
        }
    }

    public static void pushData(Intent intent, String key, Object data) {
        if (intent != null) {
            intent.putExtra(key, pushData(data));
        }
    }
}

package com.pccw.nowplayer.helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Kevin on 2016/5/18.
 */
public class DebugToast {

    static boolean isDeug = true;

    public static void toast(Context context, String msg) {
        if (isDeug)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}

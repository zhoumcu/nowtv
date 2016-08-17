package com.pccw.nowplayer.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Swifty on 7/17/2016.
 */
public class DeviceUtils {
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}

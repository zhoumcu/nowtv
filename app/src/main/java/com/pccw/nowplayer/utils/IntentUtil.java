package com.pccw.nowplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Swifty on 5/22/2016.
 */
public class IntentUtil {


    public static void call(Context context, String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (PermissionUtils.checkCallPermission(context)) {
            context.startActivity(callIntent);
        }
    }
}

package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 5/3/2016.
 */
public class RestartLinkHandler extends LinkHandler {

    public static String[] getHooks() {
        return new String[]{Constants.ACTION_RESTART};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        return true;
    }
}

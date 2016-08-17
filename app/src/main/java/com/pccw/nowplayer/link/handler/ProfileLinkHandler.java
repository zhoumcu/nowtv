package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.ProfileActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 5/6/2016.
 */
public class ProfileLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_PROFILE};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
        return true;
    }
}

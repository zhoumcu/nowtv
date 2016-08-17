package com.pccw.nowplayer.link.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.settings.NowDollarActivity;
import com.pccw.nowplayer.activity.video.VEDollarPINActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 2016/3/15.
 */
public class NowDollarLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_NOW_DOLLAR};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent = new Intent(context, NowDollarActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }

}

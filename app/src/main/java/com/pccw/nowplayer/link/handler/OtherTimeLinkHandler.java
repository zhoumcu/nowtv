package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.video.OtherTimeActivity;
import com.pccw.nowplayer.activity.video.ScreenCastActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 5/27/2016.
 */
public class OtherTimeLinkHandler extends LinkHandler {

    public static String[] getHooks() {
        return new String[]{Constants.ACTION_OTHER_TIME};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent = new Intent(context, OtherTimeActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }
}

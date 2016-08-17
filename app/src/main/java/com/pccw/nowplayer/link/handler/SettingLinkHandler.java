package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.settings.SettingActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by swifty on 30/7/16.
 */
public class SettingLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_SETTING};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        bundle = new Bundle();
        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }

}
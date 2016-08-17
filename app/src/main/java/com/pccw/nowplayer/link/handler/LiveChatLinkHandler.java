package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.mynow.LiveChatActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 6/2/2016.
 */
public class LiveChatLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_LIVE_CHAT};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, LiveChatActivity.class);
        context.startActivity(intent);
        return true;
    }
}

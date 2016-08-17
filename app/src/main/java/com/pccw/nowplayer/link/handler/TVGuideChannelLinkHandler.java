package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.TVGuideChannelDetailActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 5/14/2016.
 */
public class TVGuideChannelLinkHandler extends LinkHandler {

    public static String[] getHooks() {
        return new String[]{Constants.ACTION_TV_GUIDE_CHANNEL_DETAIL};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent = new Intent(context, TVGuideChannelDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }

}

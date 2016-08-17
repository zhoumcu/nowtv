package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.os.Bundle;

import com.pccw.nowplayer.activity.MainActivity;
import com.pccw.nowplayer.activity.tvremote.STBBingFragment;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;

/**
 * Created by Swifty on 7/17/2016.
 */
public class STBBindingLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_STB_BINDING};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (NowIDClient.getInstance().isLoggedIn()) {
            STBBingFragment fragment = new STBBingFragment();
            if (context instanceof MainActivity) {
                ((MainActivity) context).changeFragment(fragment, false);
            }
        } else {
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_LOGIN);
        }
        return true;
    }
}

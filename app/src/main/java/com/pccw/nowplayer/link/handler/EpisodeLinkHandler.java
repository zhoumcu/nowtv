package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.mynow.ForgetPasswordActivity;
import com.pccw.nowplayer.activity.node.EpisodeListActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by shuaikunwang on 22/7/16.
 */
public class EpisodeLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_EPISODE_LISTING};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, EpisodeListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }
}

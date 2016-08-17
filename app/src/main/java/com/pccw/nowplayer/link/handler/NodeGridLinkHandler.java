package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.node.NodeGridActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 5/21/2016.
 */
public class NodeGridLinkHandler extends LinkHandler {

    public static String[] getHooks() {
        return new String[]{Constants.ACTION_ALL_NODE_PAGE};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent = new Intent(context, NodeGridActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }

}

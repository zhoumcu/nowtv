package com.pccw.nowplayer.link.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.pccw.nowplayer.activity.video.VideoPlayer;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.BeginCheckout;
import com.pccw.nowplayer.model.node.Node;

/**
 * Created by Swifty on 5/27/2016.
 */
public class VideoPlayerLinkHandler extends LinkHandler {

    public static String[] getHooks() {
        return new String[]{Constants.ACTION_VIDEO_PLAYER};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        if (context instanceof AppCompatActivity && bundle.containsKey(Constants.ARG_NODE)) {
            Node node = (Node) bundle.getSerializable(Constants.ARG_NODE);
            new BeginCheckout().beginCheckout((FragmentActivity) context,node);
        }

        return true;
    }
}

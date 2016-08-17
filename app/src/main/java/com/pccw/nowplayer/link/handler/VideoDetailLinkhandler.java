package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.video.EPGVideoDetailActivity;
import com.pccw.nowplayer.activity.video.VodDetailActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.constant.VideoTypeIndex;

/**
 * Created by Swifty on 5/26/2016.
 */
public class VideoDetailLinkhandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_VIDEO_DETAIL};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent = null;
        if(String.valueOf(VideoTypeIndex.VOD).equals(link_suffix )){
            intent = new Intent(context,VodDetailActivity.class);
        }else if(String.valueOf(VideoTypeIndex.EPG).equals(link_suffix)){
            intent = new Intent(context,EPGVideoDetailActivity.class);
        }

        if (intent != null) {
            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
}

package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.NormalWebActivity;
import com.pccw.nowplayer.activity.settings.NowDollarTopUpActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowtv.nmaf.appVersionData.NMAFAppVersionDataUtils;

/**
 * Created by Swifty on 6/27/2016.
 */
public class TopUpDollarLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_TOPUP};
    }
    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, NowDollarTopUpActivity.class);
        context.startActivity(intent);
        return true;
    }
}

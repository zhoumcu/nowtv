package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.NormalWebActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowtv.nmaf.appVersionData.NMAFAppVersionDataUtils;

/**
 * Created by Swifty on 6/27/2016.
 */
public class MoreAppLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_MORE_APP};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, NormalWebActivity.class);
        intent.putExtra(Constants.BUNDLE_URL, NMAFAppVersionDataUtils.getSharedInstance().getAppInfo().getRelatedAppURL());
        intent.putExtra(Constants.ARG_SHOW_TOOLBAR, true);
        intent.putExtra(Constants.ARG_TITLE, context.getString(R.string.more_apps));
        context.startActivity(intent);
        return true;
    }
}

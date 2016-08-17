package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.NormalWebActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowtv.nmaf.appVersionData.NMAFAppVersionDataUtils;

/**
 * Created by swifty on 3/8/16.
 */
public class ServiceNoticeLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_SERVICE_NOTICE};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, NormalWebActivity.class);
        intent.putExtra(Constants.BUNDLE_URL, NMAFAppVersionDataUtils.getSharedInstance().getAppInfo().getServiceNoticeURL());
        intent.putExtra(Constants.ARG_SHOW_TOOLBAR, true);
        intent.putExtra(Constants.ARG_TITLE, context.getString(R.string.setting_service_notice));
        context.startActivity(intent);
        return true;
    }
}

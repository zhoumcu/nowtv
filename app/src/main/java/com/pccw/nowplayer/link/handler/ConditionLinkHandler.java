package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.NormalWebActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowtv.nmaf.appVersionData.NMAFAppVersionDataUtils;

/**
 * Created by Kevin on 7/12/2016.
 */
public class ConditionLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_CONDITION};
    }
    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, NormalWebActivity.class);
        intent.putExtra(Constants.BUNDLE_URL, NMAFAppVersionDataUtils.getSharedInstance().getAppInfo().getTncURL());
        context.startActivity(intent);
        return true;
    }
}

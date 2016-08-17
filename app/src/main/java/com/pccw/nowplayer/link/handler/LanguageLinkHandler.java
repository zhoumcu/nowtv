package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.settings.LanguageActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 2016/3/15.
 */
public class LanguageLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[] {Constants.ACTION_LANGUAGE};
    }
    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent = new Intent(context, LanguageActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }

}

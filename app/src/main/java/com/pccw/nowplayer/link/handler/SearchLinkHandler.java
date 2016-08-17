package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.search.SearchActivity;
import com.pccw.nowplayer.activity.search.SearchDetailActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 2016/3/15.
 */
public class SearchLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_SEARCH};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent;
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putString(Constants.ARG_SEARCH_VALUE, link_suffix);
            intent = new Intent(context, SearchActivity.class);
            intent.putExtras(bundle);
        } else {
            bundle.putString(Constants.ARG_SEARCH_VALUE, link_suffix);
            intent = new Intent(context, SearchDetailActivity.class);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        return true;
    }

}

package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.activity.MainActivity;
import com.pccw.nowplayer.activity.mynow.MyNowFragment;
import com.pccw.nowplayer.activity.tvremote.TVRemoteFragment;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.LandingFragment;
import com.pccw.nowplayer.fragment.MainBaseFragment;
import com.pccw.nowplayer.fragment.MoreFragment;
import com.pccw.nowplayer.fragment.OnDemandFragment;
import com.pccw.nowplayer.fragment.TVGuideFragment;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;

/**
 * Created by Swifty on 4/29/2016.
 */
public class MainPageLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_MAIN};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (context instanceof MainActivity) {
            if (link_suffix == null) return false;
            MainBaseFragment fragment = null;
            if (link_suffix.startsWith(Constants.ACTION_FRAGMENT_LANDING)) {
                fragment = new LandingFragment();
            } else if (link_suffix.startsWith(Constants.ACTION_FRAGMENT_TV_GUIDE)) {
                fragment = new TVGuideFragment();
            } else if (link_suffix.startsWith(Constants.ACTION_FRAGMENT_ONDEMAND)) {
                fragment = new OnDemandFragment();
            } else if (link_suffix.startsWith(Constants.ACTION_FRAGMENT_MORE)) {
                fragment = new MoreFragment();
            } else if (link_suffix.startsWith(Constants.ACTION_FRAGMENT_MY_NOW)) {
                fragment = new MyNowFragment();
                if (link_suffix.split("/").length > 1) {
                    try {
                        if (bundle == null) bundle = new Bundle();
                        bundle.putInt(Constants.ARG_INDEX, Integer.valueOf(link_suffix.split("/")[1]));
                    } catch (Exception e) {
                        Log.w(PlayerApplication.TAG, e);
                    }

                }
            } else if (link_suffix.startsWith(Constants.ACTION_FRAGMENT_TV_REMOTE)) {
                if (NowIDClient.getInstance().isLoggedIn()) {
                    fragment = new TVRemoteFragment();
                } else {
                    NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_LOGIN);
                }
            }
            if ((((MainActivity) context).getCurrentFragment()) instanceof MainBaseFragment && !((MainBaseFragment) (((MainActivity) context).getCurrentFragment())).isSameWith(fragment)) {
                if (fragment != null) {
                    fragment.setArguments(bundle);
                    ((MainActivity) context).changeFragment(fragment, true);
                }
            }
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.ARG_ACTION, link);
            context.startActivity(intent);
        }
        return true;
    }

}


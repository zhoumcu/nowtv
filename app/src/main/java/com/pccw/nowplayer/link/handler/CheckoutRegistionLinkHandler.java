package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.mynow.CheckOutRegistionActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 5/21/2016.
 */
public class CheckoutRegistionLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_CHECK_OUT_REGISTRATION};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, CheckOutRegistionActivity.class);
        intent.putExtra(Constants.ARG_CHECK_OUT_REQUEST_CODE, link_suffix);
        context.startActivity(intent);
        return true;
    }
}
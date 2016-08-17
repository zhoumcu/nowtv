package com.pccw.nowplayer.link.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.video.ParentalCtrl1stActivity;
import com.pccw.nowplayer.activity.video.VEDollarPINActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 2016/3/15.
 */
public class VEDollarLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[] {Constants.ACTION_VE_DOLLAR};
    }
    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent = new Intent(context, VEDollarPINActivity.class);
        intent.putExtras(bundle);
        if(context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent,Constants.REQUEST_VE_CODE);
        }else{
            context.startActivity(intent);
        }
        return true;
    }

}

package com.pccw.nowplayer.link.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.pccw.nowplayer.activity.video.ParentalCtrl1stActivity;
import com.pccw.nowplayer.activity.video.ParentalCtrlActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.model.CheckoutClient;
import com.pccw.nowplayer.utils.StringUtils;

/**
 * Created by Swifty on 2016/3/15.
 */
public class ParentalCtrlLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_PARENT_CTRL};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        Intent intent;
        if (bundle.getBoolean(Constants.ARG_IS_FIRSTLOCK)) {
            intent = new Intent(context, ParentalCtrl1stActivity.class);
        } else {
            intent = new Intent(context, ParentalCtrlActivity.class);
        }
        intent.putExtras(bundle);
        if (TextUtils.isEmpty(link_suffix)) {
            int requestCode = StringUtils.parseInt(link_suffix, -1);
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
        return true;
    }

}

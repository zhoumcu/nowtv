package com.pccw.nowplayer.link.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.mynow.LoginActvitiy;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.utils.TypeUtils;

/**
 * Created by Swifty on 5/8/2016.
 */
public class LoginLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_LOGIN};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, LoginActvitiy.class);
        int requestCode = TypeUtils.toInt(link_suffix, 0);
        if (requestCode != 0 && context instanceof Activity) {
            if (bundle == null) bundle = new Bundle();
            bundle.putInt(Constants.ARG_REQUEST_CODE, requestCode);
            intent.putExtras(bundle);
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
        context.startActivity(intent);
        return true;
    }
}

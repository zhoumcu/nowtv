package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.mynow.ChangePasswordActivity;
import com.pccw.nowplayer.activity.mynow.ForgetPasswordActivity;
import com.pccw.nowplayer.constant.Constants;

/**
 * Created by Swifty on 5/8/2016.
 */
public class ForgetPwdLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_FORGET_PWD};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        context.startActivity(intent);
        return true;
    }
}

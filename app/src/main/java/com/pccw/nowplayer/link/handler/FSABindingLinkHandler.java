package com.pccw.nowplayer.link.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pccw.nowplayer.activity.mynow.FSABindingActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.utils.TypeUtils;

/**
 * Created by Swifty on 5/21/2016.
 */
public class FSABindingLinkHandler extends LinkHandler {
    public static String[] getHooks() {
        return new String[]{Constants.ACTION_FSA_BINDING};
    }

    @Override
    public boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle) {
        Intent intent = new Intent(context, FSABindingActivity.class);
        int requestCode = TypeUtils.toInt(link_suffix, 0);
        if (requestCode != 0 && context instanceof Activity) {
            if (bundle == null) bundle = new Bundle();
            bundle.putInt(Constants.ARG_REQUEST_CODE, requestCode);
            intent.putExtras(bundle);
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
        return true;
    }
}

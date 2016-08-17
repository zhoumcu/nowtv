package com.pccw.nowplayer.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.node.Node;

/**
 * Created by Kevin on 2016/7/10.
 */
public class Judge {

    public static boolean isLogin(Object context) {
        if (!NowIDClient.getInstance().isLoggedIn()) {
            if (context instanceof Context) {
                NowPlayerLinkClient.getInstance().executeUrlAction((Context) context, Constants.ACTION_LOGIN + ":" + Constants.REQUEST_CODE);
            } else if (context instanceof Fragment) {
                NowPlayerLinkClient.getInstance().executeUrlAction((Context) context, Constants.ACTION_LOGIN + ":" + Constants.REQUEST_CODE);
            }
            return false;
        }
        return true;
    }

    public static boolean isFsa(Object context) {
        if (!NowIDClient.getInstance().isFSABound()) {
            if (context instanceof Context) {
                NowPlayerLinkClient.getInstance().executeUrlAction((Context) context, Constants.ACTION_FSA_BINDING + ":" + Constants.REQUEST_CODE);
            } else if (context instanceof Fragment) {
                NowPlayerLinkClient.getInstance().executeUrlAction((Context) context, Constants.ACTION_FSA_BINDING + ":" + Constants.REQUEST_CODE);

            }
            return false;
        }
        return true;
    }

    public static boolean checkVE(Object context, Node node) {
        if (!isLogin(context)) return false;
        if (!isFsa(context)) return false;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        if (context instanceof Context) {
            NowPlayerLinkClient.getInstance().executeUrlAction((Context) context, Constants.ACTION_VE_DOLLAR, bundle);
        } else if (context instanceof Fragment) {
            NowPlayerLinkClient.getInstance().executeUrlAction((Context) context, Constants.ACTION_VE_DOLLAR, bundle);
        }
        return false;
    }


}

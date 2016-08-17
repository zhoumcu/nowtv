package com.pccw.nowplayer.model;

import com.pccw.nowtv.nmaf.pushNotification.NMAFPushNotification;

/**
 * Created by kriz on 2016-07-21.
 */
public class PushClient {

    private static PushClient instance;

    private PushClient() {
    }

    public static PushClient getInstance() {
        if (instance == null) {
            synchronized (DownloadClient.class) {
                if (instance == null) instance = new PushClient();
            }
        }
        return instance;
    }

    public boolean isAlertOn() {
        return NMAFPushNotification.getSharedInstance().isPushAlertOn();
    }

    public void setAlertOn(boolean on) {
        NMAFPushNotification.getSharedInstance().updatePushSettings(on, 1);
    }
}

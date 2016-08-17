package com.pccw.nowplayer.service;

import android.content.Context;

import com.pccw.nowplayer.model.DownloadClient;
import com.pccw.nowplayer.utils.Pref;

/**
 * Created by Kevin on 2016/5/5.
 */
public class ConfigService {

    Pref pref;
    private final String KEY_DOWNLOAD_LOCATION = "KEY_DOWNLOAD_LOCATION";
    private final String KEY_LANGUAGE = "KEY_LANGUAGE";
    private final String KEY_PUSH_NOTIFICATION = "KEY_PUSH_NOTIFICATION";
    private final String KEY_FIRST_RUN = "KEY_FIRST_RUN";

    public ConfigService(Context context) {
        pref = Pref.getPref();
    }


    public boolean isEnglish() {
        return pref.getBool(KEY_LANGUAGE);
    }

    public void setLanguageIsCN() {
        pref.putBool(KEY_LANGUAGE, false);
    }

    public void setLanguageIsEN() {
        pref.putBool(KEY_LANGUAGE, true);
    }


    public boolean isStorePhone() {
        return pref.getBool(KEY_DOWNLOAD_LOCATION);
    }

    public void setStoreSdcard() {
        pref.putBool(KEY_DOWNLOAD_LOCATION, false);
    }

    public void setStorePhone() {
        pref.putBool(KEY_DOWNLOAD_LOCATION, true);
    }


    public boolean isMobileDataEnabled() {
        return DownloadClient.getInstance().isMobileDataEnabled();
    }

    public void setUnderMobile(boolean isUnderMobile) {
        DownloadClient.getInstance().setMobileDataEnabled(isUnderMobile);
    }

    public boolean isPushNotification() {
        return pref.getBool(KEY_PUSH_NOTIFICATION);
    }

    public void setPushNotification(boolean isNotification) {
        pref.putBool(KEY_PUSH_NOTIFICATION, isNotification);
    }

    public boolean isChangedLanguage() {
        return pref.getBool(KEY_FIRST_RUN, false);
    }

    public void setChangedLanguage(boolean isFirst) {
        pref.putBool(KEY_FIRST_RUN, isFirst);
    }

}

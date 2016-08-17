package com.pccw.nowplayer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.webkit.WebView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.link.handler.ChangePasswordLinkHandler;
import com.pccw.nowplayer.link.handler.CheckoutRegistionLinkHandler;
import com.pccw.nowplayer.link.handler.ConditionLinkHandler;
import com.pccw.nowplayer.link.handler.DownloadLocationLinkHandler;
import com.pccw.nowplayer.link.handler.EpisodeLinkHandler;
import com.pccw.nowplayer.link.handler.FSABindingLinkHandler;
import com.pccw.nowplayer.link.handler.ForgetPwdLinkHandler;
import com.pccw.nowplayer.link.handler.LanguageLinkHandler;
import com.pccw.nowplayer.link.handler.LiveChatLinkHandler;
import com.pccw.nowplayer.link.handler.LoginLinkHandler;
import com.pccw.nowplayer.link.handler.MainPageLinkHandler;
import com.pccw.nowplayer.link.handler.MoreAppLinkHandler;
import com.pccw.nowplayer.link.handler.NodeGridLinkHandler;
import com.pccw.nowplayer.link.handler.NodeListLinkHandler;
import com.pccw.nowplayer.link.handler.NowDollarLinkHandler;
import com.pccw.nowplayer.link.handler.NowIDLinkHandler;
import com.pccw.nowplayer.link.handler.OfflineLinkHandler;
import com.pccw.nowplayer.link.handler.OtherTimeLinkHandler;
import com.pccw.nowplayer.link.handler.ParentalCtrlLinkHandler;
import com.pccw.nowplayer.link.handler.ProfileLinkHandler;
import com.pccw.nowplayer.link.handler.RestartLinkHandler;
import com.pccw.nowplayer.link.handler.STBBindingLinkHandler;
import com.pccw.nowplayer.link.handler.ScreenCastLinkHandler;
import com.pccw.nowplayer.link.handler.SearchLinkHandler;
import com.pccw.nowplayer.link.handler.ServiceNoticeLinkHandler;
import com.pccw.nowplayer.link.handler.SettingLinkHandler;
import com.pccw.nowplayer.link.handler.TVGuideChannelLinkHandler;
import com.pccw.nowplayer.link.handler.TopUpDollarLinkHandler;
import com.pccw.nowplayer.link.handler.VEDollarLinkHandler;
import com.pccw.nowplayer.link.handler.VideoDetailLinkhandler;
import com.pccw.nowplayer.link.handler.VideoPlayerLinkHandler;
import com.pccw.nowplayer.link.handler.WebLinkHandler;
import com.pccw.nowplayer.link.handler.YourBoxLinkHandler;
import com.pccw.nowplayer.model.db_orm.OrmController;
import com.pccw.nowplayer.service.ConfigService;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowtv.nmaf.appVersionData.NMAFAppVersionDataUtils;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.core.NMAFFramework;
import com.pccw.nowtv.nmaf.npx.NPX;
import com.pccw.nowtv.nmaf.utilities.NMAFLanguageUtils;
import com.pccw.nowtv.nmaf.utilities.NMAFLogging;

import java.util.HashMap;

/**
 * Created by Swifty on 2016/3/15.
 */
public class PlayerApplication extends MultiDexApplication {
    public static final String TAG = "NMAF";
    private static Context context;

    public static boolean needRefreshFragment;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Fabric.with(this, new Crashlytics());
        registerLink();
        OrmController.initORM(this);
        initPref();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

//        PgyCrashManager.register(this);
    }

    private void initPref() {
        Pref.getPref().putBool(Constants.PREF_SHOW_STREAM_QUALITY, true);
    }


    public static void initLanguage() {
        ConfigService configService = new ConfigService(context);
        if (configService.isChangedLanguage()) {
            NMAFLanguageUtils.getSharedInstance().setLanguage(configService.isEnglish() ? "en" : "zh");
        }
    }


    public static void initNMAF(NMAFBaseModule.ResultCallback resultCallback) {
        HashMap<String, String> initParams = new HashMap<>();
        initParams.put(NMAFFramework.NMAFInitParam_BuildType, NMAFFramework.BuildType.SIT.toString());
        initParams.put(NMAFAppVersionDataUtils.NMAFAVDUInitParam_AppId, "X1");
        initParams.put(NMAFLogging.NMAFLGInitParam_DisableUncaughtExceptionHandler, "Y");
        initParams.put(NMAFLanguageUtils.NMAFLUInitParam_Languages, "{\"zh\":[\"zh\",\"zh_TW\",\"zh_CN\",\"zh_HK\",\"zh_SG\"],\"en\":[\"en\",\"en_US\",\"en_GB\"]}");

        NMAFFramework.getSharedInstance().initializeFramework(context, initParams, new Class[]{NPX.class}, resultCallback);
    }

    private void registerLink() {
        NowPlayerLinkClient.getInstance().registerLink(SearchLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(MainPageLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(RestartLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(DownloadLocationLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(LanguageLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(NowIDLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(YourBoxLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(ParentalCtrlLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(VEDollarLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(ProfileLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(LoginLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(ChangePasswordLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(ForgetPwdLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(TVGuideChannelLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(WebLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(NodeListLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(NodeGridLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(FSABindingLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(NowDollarLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(TopUpDollarLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(CheckoutRegistionLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(VideoDetailLinkhandler.class);
        NowPlayerLinkClient.getInstance().registerLink(VideoPlayerLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(ScreenCastLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(OtherTimeLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(LiveChatLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(MoreAppLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(ConditionLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(STBBindingLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(OfflineLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(EpisodeLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(SettingLinkHandler.class);
        NowPlayerLinkClient.getInstance().registerLink(ServiceNoticeLinkHandler.class);
    }

    public static Context getContext() {
        return context;
    }
}

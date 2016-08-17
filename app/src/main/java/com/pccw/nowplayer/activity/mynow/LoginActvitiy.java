package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.activity.NowTVWebViewActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

/**
 * Created by Swifty on 5/8/2016.
 */
public class LoginActvitiy extends NowTVWebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLogin();
    }

    public void startLogin() {
        setWebViewClient(webViewClient);
        url = NMAFnowID.getSharedInstance().getLoginUrl(SUCCESS, CANCEL);
        loadUrl(url);
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(SUCCESS)) {
                progress.setVisibility(View.VISIBLE);
                Pref.getPref().putBool(Constants.PREF_ISLOGIND, true);
                setResult(Constants.SUCCESS_CODE);
                NMAFnowID.getSharedInstance().linkLoginNowID(errorCallback);
                return true;
            } else if (url.contains(CANCEL)) {
                Pref.getPref().putBool(Constants.PREF_ISLOGIND, false);
                finish();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    private NMAFBaseModule.ErrorCallback errorCallback = new NMAFBaseModule.ErrorCallback() {
        @Override
        public void operationComplete(Throwable throwable) {
            NowIDClient.getInstance().updateSessionData().always(new AlwaysCallback() {
                @Override
                public void onAlways(Promise.State state, Object resolved, Object rejected) {
                    progress.setVisibility(View.GONE);
                    PlayerApplication.needRefreshFragment = true;
                    finish();
                }
            });
        }
    };
}

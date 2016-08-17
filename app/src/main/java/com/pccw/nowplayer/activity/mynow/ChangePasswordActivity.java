package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pccw.nowplayer.activity.NowTVWebViewActivity;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

/**
 * Created by Swifty on 5/8/2016.
 */
public class ChangePasswordActivity extends NowTVWebViewActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startChangePassord();
    }

    public void startChangePassord() {
        setWebViewClient(webViewClient);
        url = NMAFnowID.getSharedInstance().getChangePasswordUrl(SUCCESS, CANCEL);
        loadUrl(url);
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(SUCCESS)) {
                NMAFnowID.getSharedInstance().updateSession(errorCallback);
                return true;
            } else if (url.contains(CANCEL)) {
                finish();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    private NMAFBaseModule.ErrorCallback errorCallback = new NMAFBaseModule.ErrorCallback() {
        @Override
        public void operationComplete(Throwable throwable) {
            if (throwable == null) {
                finish();
            }
        }
    };
}

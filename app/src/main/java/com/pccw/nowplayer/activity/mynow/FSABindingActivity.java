package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pccw.nowplayer.activity.NowTVWebViewActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

/**
 * Created by Swifty on 5/21/2016.
 */
public class FSABindingActivity extends NowTVWebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSTBBinding();
    }

    public void startSTBBinding() {
        setWebViewClient(webViewClient);
        url = NMAFnowID.getSharedInstance().getSTBBindingUrl(SUCCESS, CANCEL);
        loadUrl(url);
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(SUCCESS)) {
                Pref.getPref().putBoolSync(Constants.PREF_ISSTBBIND, true);
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
                if (request_code != 0)
                    setResult(request_code);
                finish();
            }
        }
    };
}


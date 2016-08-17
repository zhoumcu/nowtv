package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pccw.nowplayer.activity.NowTVWebViewActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

/**
 * Created by Swifty on 5/21/2016.
 */
public class CheckOutRegistionActivity extends NowTVWebViewActivity {
    String requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCode = getIntent().getStringExtra(Constants.ARG_CHECK_OUT_REQUEST_CODE);
        startDeviceRegistration();
    }

    public void startDeviceRegistration() {
        setWebViewClient(webViewClient);
        NMAFBasicCheckout.getSharedInstance().setDeviceControlRequest(webview, requestCode, SUCCESS, CANCEL);
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(SUCCESS)) {
                setResult(Constants.SUCCESS_CODE);
                finish();
                return true;
            } else if (url.contains(CANCEL)) {
                finish();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };
}

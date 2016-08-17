package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pccw.nowplayer.activity.NowTVWebViewActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

/**
 * Created by Swifty on 6/2/2016.
 */
public class LiveChatWebActivity extends NowTVWebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLiveChat(getIntent().getStringExtra(Constants.ARG_LIVE_CHAT_URL));
        showToolBar(true);
    }

    public void startLiveChat(String stringExtra) {
        if (!TextUtils.isEmpty(stringExtra)) {
            url = stringExtra;
            loadUrl(url);
        }
    }
}

package com.pccw.nowplayer.activity.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.NowTVWebViewActivity;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

/**
 * Created by Kevin on 5/8/2016.
 */
public class NowDollarTopUpActivity extends NowTVWebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTopUp();
    }

    public void startTopUp() {
        setWebViewClient(webViewClient);
        NowIDClient.getInstance().loadNowDollarTopUpUrl().then(new DoneCallback<String>(){
            @Override
            public void onDone(String result) {
                L.e(result);
                loadUrl(result);
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                DialogHelper.createRequstFailDialog(NowDollarTopUpActivity.this).show();
            }
        });
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
        }
    };

}

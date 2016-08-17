package com.pccw.nowplayer.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/7/2016.
 */
public class NowTVWebViewActivity extends ToolBarBaseActivity {
    protected final String CANCEL = "NOWIDV2CANCELTAG";
    protected final String SUCCESS = "NOWIDV2SUCCESSTAG";
    protected NMAFBaseModule.ErrorCallback errorCallback = new NMAFBaseModule.ErrorCallback() {
        @Override
        public void operationComplete(Throwable throwable) {
            if (throwable == null) {
                finish();
            }
        }
    };
    @Bind(R.id.progress_lay)
    protected View progress;
    protected int request_code;
    protected String url;
    @Bind(R.id.webview)
    protected WebView webview;
    @Bind(R.id.title)
    TextView title;

    @Override
    protected void bindEvents() {

    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        request_code = getIntent().getIntExtra(Constants.ARG_REQUEST_CODE, 0);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        setWebViewSettings(webview);
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        showToolBar(false);
    }

    public void loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            webview.loadUrl(url);
        }
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setWebChromeSetting(WebChromeClient webChromeSetting) {
        if (webChromeSetting != null)
            webview.setWebChromeClient(webChromeSetting);
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        if (webViewClient != null)
            webview.setWebViewClient(webViewClient);
    }

    private void setWebViewSettings(final WebView webView) {
        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }
        settings.setAppCacheEnabled(true);
    }

    protected void showToolBar(boolean show) {
        toolbar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

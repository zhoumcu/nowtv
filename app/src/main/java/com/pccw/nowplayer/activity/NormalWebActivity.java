package com.pccw.nowplayer.activity;

import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.utils.Check;

/**
 * Created by Kevin on 2016/5/21.
 */
public class NormalWebActivity extends NowTVWebViewActivity {


    @Override
    protected void bindEvents() {
        super.bindEvents();
        showToolBar(getIntent().getBooleanExtra(Constants.ARG_SHOW_TOOLBAR, false));
        setTitle(getIntent().getStringExtra(Constants.ARG_TITLE));
        if (Check.isEmpty(url)) {
            finish();
        }
        loadUrl(url);
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        url = getIntent().getStringExtra(Constants.BUNDLE_URL);
    }
}

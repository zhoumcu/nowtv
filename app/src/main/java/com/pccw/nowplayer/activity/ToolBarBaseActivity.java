package com.pccw.nowplayer.activity;

import android.support.v7.widget.Toolbar;

import com.pccw.nowplayer.R;

import butterknife.Bind;

/**
 * Created by Swifty on 5/19/2016.
 */
public abstract class ToolBarBaseActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
    }


}

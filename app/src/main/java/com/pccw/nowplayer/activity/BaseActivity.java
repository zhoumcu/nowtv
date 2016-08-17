package com.pccw.nowplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.core.NMAFFramework;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;
import com.pccw.nowtv.nmaf.utilities.NMAFLanguageUtils;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * Created by Swifty on 2016/3/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public String TAG = this.getClass().getSimpleName();
    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needRestart()) {
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_RESTART);
        } else {
            NMAFLanguageUtils.getSharedInstance().applyLanguage(this);
            initIntentData();
            initViews();
            initToolBar();
            bindEvents();
        }
    }

    protected void initIntentData() {
    }

    protected abstract void initViews();

    protected abstract void initToolBar();

    protected abstract void bindEvents();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean needRestart() {
        try {
            Field field = NMAFFramework.class.getDeclaredField("modules");
            field.setAccessible(true);
            return field.get(NMAFFramework.getSharedInstance()) == null;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}

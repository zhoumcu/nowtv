package com.pccw.nowplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.settings.LanguageActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Swifty on 5/21/2016.
 */
public class MoreFragment extends MainBaseFragment {

    @Bind(R.id.bpl_setting_btn)
    LinearLayout bplSettingBtn;
    @Bind(R.id.more_app_btn)
    LinearLayout moreAppBtn;

    @Override
    public View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.fragment_more, parentContainer, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.bpl_setting_btn)
    public void gotoBPLSetting() {
        NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_BPL_SETTING);
    }

    @OnClick(R.id.more_app_btn)
    public void gotoMoreApps() {
        NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_MORE_APP);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public ActionBar showActionBar() {
        return new ActionBar(false, getString(R.string.more), false);
    }
}

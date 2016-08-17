package com.pccw.nowplayer.activity.mynow;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NowIDActivity extends ThemeActivity {

    @Bind(R.id.ll_setting_now_id)
    LinearLayout llSettingNowId;
    @Bind(R.id.logout)
    Button logout;
    @Bind(R.id.tv_now_id)
    TextView tvNowId;

    @Override
    protected void bindEvents() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowIDClient.getInstance().logout();
                PlayerApplication.needRefreshFragment = true;
                finish();
            }
        });
        llSettingNowId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(NowIDActivity.this, Constants.ACTION_CHANGE_PWD);
            }
        });
    }

    @Override
    protected void initViews() {
        setViewUnderToolbar(R.layout.activity_now_id);
        ButterKnife.bind(this);
        tvNowId.setText(NowIDClient.getInstance().getNowId());

    }
}

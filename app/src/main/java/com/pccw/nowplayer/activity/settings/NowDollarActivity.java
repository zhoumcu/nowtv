package com.pccw.nowplayer.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ToolBarBaseActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/7/13.
 */
public class NowDollarActivity extends ToolBarBaseActivity implements View.OnClickListener {


    @Bind(R.id.bt_top_up)
    Button btTopUp;
    @Bind(R.id.subtitle)
    TextView subtitle;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_lay)
    LinearLayout titleLay;
    @Bind(R.id.tv_setting_dollar_balance)
    TextView tvSettingDollarBalance;
    @Bind(R.id.tv_setting_expiry_date)
    TextView tvSettingExpiryDate;

    @Override
    protected void bindEvents() {
        btTopUp.setOnClickListener(this);
    }

    @Override
    protected void initViews() {

        setContentView(R.layout.activity_now_dollar);
        ButterKnife.bind(this);
        title.setText(getString(R.string.title_activity_now_dollar));
        refresh();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_top_up) {
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_TOPUP);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NMAFnowID.getSharedInstance().updateSession(new NMAFBaseModule.ErrorCallback() {
            @Override
            public void operationComplete(Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

    }

    private void refresh() {
        tvSettingDollarBalance.setText(String.format(getString(R.string.dollar), StringUtils.formatFloat(NowIDClient.getInstance().getNowDollarBalance())));
        long expire = NowIDClient.getInstance().getDollarExpiry();
        if (expire <= 0) {
            tvSettingExpiryDate.setText(R.string.na);
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            tvSettingExpiryDate.setText(simpleDateFormat.format(new Date(expire)));
        }
    }
}

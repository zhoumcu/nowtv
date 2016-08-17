package com.pccw.nowplayer.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.activity.mynow.NowIDActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.service.ConfigService;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowplayer.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kevin on 2016/5/5.
 */
public class SettingActivity extends ThemeActivity implements CompoundButton.OnCheckedChangeListener {

    ConfigService configService;
    @Bind(R.id.ll_setting_download_location)
    LinearLayout llSettingDownloadLocation;
    @Bind(R.id.ll_setting_language)
    LinearLayout llSettingLanguage;
    @Bind(R.id.ll_setting_now_dollar)
    LinearLayout llSettingNowDollar;
    @Bind(R.id.ll_setting_now_id)
    LinearLayout llSettingNowId;
    @Bind(R.id.ll_setting_push_notification)
    LinearLayout llSettingPushNotification;
    @Bind(R.id.ll_setting_service_notice)
    LinearLayout llSettingServiceNotice;
    @Bind(R.id.ll_setting_under_mobile)
    LinearLayout llSettingUnderMobile;
    @Bind(R.id.ll_setting_version)
    LinearLayout llSettingVersion;
    @Bind(R.id.ll_setting_your_box)
    LinearLayout llSettingYourBox;
    int requestCode = 123;
    @Bind(R.id.sb_setting_push_notification_value)
    SwitchButton sbSettingPushNotificationValue;
    @Bind(R.id.sb_setting_under_mobile_value)
    SwitchButton sbSettingUnderMobileValue;
    @Bind(R.id.tv_setting_download_location_value)
    TextView tvSettingDownloadLocationValue;
    @Bind(R.id.tv_setting_language_value)
    TextView tvSettingLanguageValue;
    @Bind(R.id.tv_setting_login_tips)
    TextView tvSettingLoginTips;
    @Bind(R.id.tv_setting_now_dollar)
    TextView tvSettingNowDollar;
    @Bind(R.id.tv_setting_now_id_value)
    TextView tvSettingNowIdValue;
    @Bind(R.id.tv_setting_version_value)
    TextView tvSettingVersionValue;
    @Bind(R.id.tv_setting_your_box_value)
    TextView tvSettingYourBoxValue;

    @Override
    protected void bindEvents() {

    }

    @OnClick(R.id.ll_setting_download_location)
    public void downloadLocation() {
        intentTo(DownloadLocationActivity.class);
    }

    private void initView() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            tvSettingVersionValue.setText(version);
            sbSettingUnderMobileValue.setOnCheckedChangeListener(this);
            sbSettingPushNotificationValue.setOnCheckedChangeListener(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        refreshView();
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.fragment_setting_main);
        ButterKnife.bind(this);
        configService = new ConfigService(this);
        initView();
        setTitle(getString(R.string.setting));
    }

    private void intentTo(Class<? extends Activity> activity) {
        Intent intent = new Intent(this, activity);
        startActivityForResult(intent, requestCode);
    }

    @OnClick(R.id.ll_setting_language)
    public void language() {
        Intent intent = new Intent(this, LanguageActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_setting_now_dollar)
    public void nowDollar() {
        NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_NOW_DOLLAR);
    }

    @OnClick(R.id.ll_setting_now_id)
    public void nowId() {
        if (NowIDClient.getInstance().isLoggedIn()) {
            intentTo(NowIDActivity.class);
        } else {
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_LOGIN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode) {
            initView();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.sb_setting_under_mobile_value) {
            configService.setUnderMobile(isChecked);
        } else if (buttonView.getId() == R.id.sb_setting_push_notification_value) {
            configService.setPushNotification(isChecked);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NowIDClient.getInstance().isLoggedIn()) {
            tvSettingNowIdValue.setText(NowIDClient.getInstance().getNowId());
            tvSettingLoginTips.setVisibility(View.GONE);
            llSettingYourBox.setVisibility(View.VISIBLE);
            llSettingNowDollar.setVisibility(View.VISIBLE);
            String fsa = NowIDClient.getInstance().getFsa();
            if (!Check.isEmpty(fsa)) {
                tvSettingYourBoxValue.setText(fsa);
            } else {
                tvSettingYourBoxValue.setText(getString(R.string.not_connected));
            }

        } else {
            tvSettingNowIdValue.setText(R.string.setting_please_login);
            llSettingNowDollar.setVisibility(View.GONE);
            tvSettingLoginTips.setVisibility(View.VISIBLE);
            llSettingYourBox.setVisibility(View.GONE);
        }
        refreshView();
    }

    @OnClick(R.id.ll_setting_service_notice)
    public void openServiceNotice() {
        NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_SERVICE_NOTICE);
    }

    private void refreshView() {
        tvSettingLanguageValue.setText(configService.isEnglish() ? R.string.setting_english : R.string.setting_chinese);
        tvSettingDownloadLocationValue.setText(configService.isStorePhone() ? R.string.setting_phone_memory : R.string.setting_sdcard_memory);
        sbSettingUnderMobileValue.setChecked(configService.isMobileDataEnabled());
        sbSettingPushNotificationValue.setChecked(configService.isPushNotification());
        tvSettingNowDollar.setText(String.format(getString(R.string.dollar), StringUtils.formatFloat(NowIDClient.getInstance().getNowDollarBalance())));
    }

    @OnClick(R.id.ll_setting_your_box)
    public void yourBox() {
        NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_YOUR_BOX);
    }
}

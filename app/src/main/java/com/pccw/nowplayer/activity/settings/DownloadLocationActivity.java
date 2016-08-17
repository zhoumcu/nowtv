package com.pccw.nowplayer.activity.settings;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.service.ConfigService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DownloadLocationActivity extends ThemeActivity implements View.OnClickListener {


    @Bind(R.id.rb_setting_phone)
    RadioButton rbSettingPhone;
    @Bind(R.id.cb_setting_phone)
    CheckBox cbSettingPhone;
    @Bind(R.id.ll_setting_phone)
    LinearLayout llSettingPhone;
    @Bind(R.id.rb_setting_sdcard)
    RadioButton rbSettingSdcard;
    @Bind(R.id.cb_setting_sdcard)
    CheckBox cbSettingSdcard;
    @Bind(R.id.ll_setting_sdcard)
    LinearLayout llSettingSdcard;
    ConfigService configService;

    @Override
    protected void initViews() {
        setViewUnderToolbar(R.layout.fragment_storage_location);
        ButterKnife.bind(this, this);


        configService = new ConfigService(this);
        toggle(configService.isStorePhone());
    }

    @Override
    protected void bindEvents() {
        llSettingPhone.setOnClickListener(this);
        llSettingSdcard.setOnClickListener(this);
    }

    private void toggle(boolean isPhone) {
        rbSettingPhone.setChecked(isPhone);
        rbSettingSdcard.setChecked(!isPhone);
        cbSettingPhone.setChecked(isPhone);
        cbSettingSdcard.setChecked(!isPhone);

        if (isPhone) {
            configService.setStorePhone();
        } else {
            configService.setStoreSdcard();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_setting_phone) {
            toggle(true);
        } else if (v.getId() == R.id.ll_setting_sdcard) {
            toggle(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

package com.pccw.nowplayer.activity.settings;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.MainActivity;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.service.ConfigService;
import com.pccw.nowplayer.utils.LocaleUtils;
import com.pccw.nowtv.nmaf.utilities.NMAFLanguageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LanguageActivity extends ThemeActivity implements View.OnClickListener {

    @Bind(R.id.rb_setting_english)
    RadioButton rbSettingEnglish;
    @Bind(R.id.rb_setting_chinese)
    RadioButton rbSettingChinese;

    @Bind(R.id.cb_setting_english)
    CheckBox cbSettingEnglish;
    @Bind(R.id.cb_setting_chinese)
    CheckBox cbSettingChinese;

    @Bind(R.id.ll_setting_english)
    LinearLayout llSettingEnglish;

    @Bind(R.id.ll_setting_chinese)
    LinearLayout llSettingChiese;

    ConfigService configService;


    @Override
    protected void initViews() {
        setViewUnderToolbar(R.layout.fragment_language);
        ButterKnife.bind(this);

        Log.e(TAG, LocaleUtils.getCurrentLocale(this) + "");

        configService = new ConfigService(this);
        toggle(configService.isEnglish());
    }


    @Override
    protected void bindEvents() {

        llSettingEnglish.setOnClickListener(this);
        llSettingChiese.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_setting_english) {
            changeLanguage(true);
            toggle(true);
        } else if (v.getId() == R.id.ll_setting_chinese) {
            changeLanguage(false);
            toggle(false);
        }
    }

    private void changeLanguage(boolean isEnglish) {
        PlayerApplication.needRefreshFragment = true;
        LocaleUtils.changeLanguage(this, isEnglish, MainActivity.class);
        if (isEnglish) {
            configService.setLanguageIsEN();
        } else {
            configService.setLanguageIsCN();
        }
        configService.setChangedLanguage(true);
        NMAFLanguageUtils.getSharedInstance().setLanguage(isEnglish ? "en" : "zh");
    }

    private void toggle(boolean isEnglish) {
        rbSettingEnglish.setChecked(isEnglish);
        rbSettingChinese.setChecked(!isEnglish);
        cbSettingEnglish.setChecked(isEnglish);
        cbSettingChinese.setChecked(!isEnglish);

        rbSettingEnglish.setText(R.string.setting_english);
        rbSettingChinese.setText(R.string.setting_chinese);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

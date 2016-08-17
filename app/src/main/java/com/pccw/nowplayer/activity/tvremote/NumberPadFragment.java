package com.pccw.nowplayer.activity.tvremote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.fragment.BaseFragment;
import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.stbCompanion.NMAFSTBCompanion;
import com.pccw.nowtv.nmaf.stbCompanion.NMAFSTBCompanionLegacy;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by swifty on 27/5/2016.
 */
public class NumberPadFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.key_0)
    ImageView key0;
    @Bind(R.id.key_1)
    ImageView key1;
    @Bind(R.id.key_2)
    ImageView key2;
    @Bind(R.id.key_3)
    ImageView key3;
    @Bind(R.id.key_4)
    ImageView key4;
    @Bind(R.id.key_5)
    ImageView key5;
    @Bind(R.id.key_6)
    ImageView key6;
    @Bind(R.id.key_7)
    ImageView key7;
    @Bind(R.id.key_8)
    ImageView key8;
    @Bind(R.id.key_9)
    ImageView key9;
    @Bind(R.id.key_no_voice)
    ImageView keyNoVoice;
    @Bind(R.id.key_polygon)
    ImageView keyPolygon;
    @Bind(R.id.vol_down)
    ImageView volDown;
    @Bind(R.id.vol_up)
    ImageView volUp;
    @Bind(R.id.subs)
    TextView subs;
    @Bind(R.id.ch_add)
    ImageView chAdd;
    @Bind(R.id.ch_down)
    ImageView chDown;
    @Bind(R.id.audio)
    TextView audio;
    private View root;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        NMAFSTBCompanionLegacy.Keycode key = null;
        if (id == R.id.key_0) {
            key = NMAFSTBCompanionLegacy.Keycode.RC0;
        } else if (id == R.id.key_1) {
            key = NMAFSTBCompanionLegacy.Keycode.RC1;
        } else if (id == R.id.key_2) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2;
        } else if (id == R.id.key_3) {
            key = NMAFSTBCompanionLegacy.Keycode.RC3;
        } else if (id == R.id.key_4) {
            key = NMAFSTBCompanionLegacy.Keycode.RC4;
        } else if (id == R.id.key_5) {
            key = NMAFSTBCompanionLegacy.Keycode.RC5;
        } else if (id == R.id.key_6) {
            key = NMAFSTBCompanionLegacy.Keycode.RC6;
        } else if (id == R.id.key_7) {
            key = NMAFSTBCompanionLegacy.Keycode.RC7;
        } else if (id == R.id.key_8) {
            key = NMAFSTBCompanionLegacy.Keycode.RC8;
        } else if (id == R.id.key_9) {
            key = NMAFSTBCompanionLegacy.Keycode.RC9;
        } else if (id == R.id.key_no_voice) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_MUTE;
        } else if (id == R.id.key_polygon) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_FREETV;
        } else if (id == R.id.audio) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_AUDIO;
        } else if (id == R.id.ch_add) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_CHANNELUP;
        } else if (id == R.id.ch_down) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_CHANNELDN;
        } else if (id == R.id.subs) {
            key = NMAFSTBCompanionLegacy.Keycode.RCSUBTITLE;
        } else if (id == R.id.vol_down) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_VOLUMEDN;
        } else if (id == R.id.vol_up) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_VOLUMEUP;
        }
        if (key != null) {
            NMAFSTBCompanion.getSharedInstance().sendKeyCode(DeviceManager.getInstance().getConnectDevice().changeToNowDevice(), key.getValue(), new NMAFBaseModule.ErrorCallback() {
                @Override
                public void operationComplete(Throwable throwable) {

                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_number_pad, container, false);
        ButterKnife.bind(this, root);
        bindViews();
        return root;
    }

    private void bindViews() {
        audio.setOnClickListener(this);
        chAdd.setOnClickListener(this);
        chDown.setOnClickListener(this);
        volDown.setOnClickListener(this);
        volUp.setOnClickListener(this);
        subs.setOnClickListener(this);
        key0.setOnClickListener(this);
        key1.setOnClickListener(this);
        key2.setOnClickListener(this);
        key3.setOnClickListener(this);
        key4.setOnClickListener(this);
        key5.setOnClickListener(this);
        key6.setOnClickListener(this);
        key7.setOnClickListener(this);
        key8.setOnClickListener(this);
        key9.setOnClickListener(this);
        keyNoVoice.setOnClickListener(this);
        keyPolygon.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

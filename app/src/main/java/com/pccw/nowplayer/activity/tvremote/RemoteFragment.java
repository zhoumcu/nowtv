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
public class RemoteFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.add)
    TextView add;
    @Bind(R.id.back)
    TextView back;
    @Bind(R.id.blue)
    View blue;
    @Bind(R.id.down)
    ImageView down;
    @Bind(R.id.green)
    View green;
    @Bind(R.id.home)
    TextView home;
    @Bind(R.id.info)
    TextView info;
    @Bind(R.id.left)
    ImageView left;
    @Bind(R.id.my_now)
    TextView myNow;
    @Bind(R.id.ok)
    ImageView ok;
    @Bind(R.id.on_demand)
    TextView onDemand;
    @Bind(R.id.red)
    View red;
    @Bind(R.id.right)
    ImageView right;
    @Bind(R.id.tv_guide)
    TextView tvGuide;
    @Bind(R.id.up)
    ImageView up;
    @Bind(R.id.yellow)
    View yellow;
    @Bind(R.id.key_quick_back)
    ImageView key_quick_back;
    @Bind(R.id.key_play)
    ImageView key_play;
    @Bind(R.id.key_quick_play)
    ImageView key_quick_play;
    @Bind(R.id.key_restart)
    TextView key_restart;
    @Bind(R.id.key_exit)
    TextView key_exit;
    @Bind(R.id.key_record)
    TextView key_record;
    @Bind(R.id.key_now)
    TextView key_now;
    @Bind(R.id.key_tv)
    TextView key_tv;
    private View root;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        NMAFSTBCompanionLegacy.Keycode key = null;
        if (id == R.id.add) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_ADD;
        } else if (id == R.id.back) {
            key = NMAFSTBCompanionLegacy.Keycode.RCBACK;
        } else if (id == R.id.blue) {
            key = NMAFSTBCompanionLegacy.Keycode.RCBLUE;
        } else if (id == R.id.down) {
            key = NMAFSTBCompanionLegacy.Keycode.RCDOWN;
        } else if (id == R.id.green) {
            key = NMAFSTBCompanionLegacy.Keycode.RCGREEN;
        } else if (id == R.id.home) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_HOME;
        } else if (id == R.id.info) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_INFO;
        } else if (id == R.id.left) {
            key = NMAFSTBCompanionLegacy.Keycode.RCLEFT;
        } else if (id == R.id.my_now) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_WATCHLIST;
        } else if (id == R.id.ok) {
            key = NMAFSTBCompanionLegacy.Keycode.RCENTER;
        } else if (id == R.id.on_demand) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_ONDEMAND;
        } else if (id == R.id.red) {
            key = NMAFSTBCompanionLegacy.Keycode.RCRED;
        } else if (id == R.id.right) {
            key = NMAFSTBCompanionLegacy.Keycode.RCRIGHT;
        } else if (id == R.id.tv_guide) {
            key = NMAFSTBCompanionLegacy.Keycode.RCTVGUIDE;
        } else if (id == R.id.up) {
            key = NMAFSTBCompanionLegacy.Keycode.RCUP;
        } else if (id == R.id.yellow) {
            key = NMAFSTBCompanionLegacy.Keycode.RCYELLOW;
        } else if (id == R.id.key_quick_back) {
            key = NMAFSTBCompanionLegacy.Keycode.RCFASTREWIND;
        } else if (id == R.id.key_play) {
            key = NMAFSTBCompanionLegacy.Keycode.RCPLAYPAUSE;
        } else if (id == R.id.key_quick_play) {
            key = NMAFSTBCompanionLegacy.Keycode.RCFASTFORWARD;
        } else if (id == R.id.key_restart) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_RESTART;
        } else if (id == R.id.key_exit) {
            key = NMAFSTBCompanionLegacy.Keycode.RCEXIT;
        } else if (id == R.id.key_record) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_RECORD;
        } else if (id == R.id.key_now) {
            key = NMAFSTBCompanionLegacy.Keycode.RCPOWER;
        } else if (id == R.id.key_tv) {
            key = NMAFSTBCompanionLegacy.Keycode.RC2_TVPOWER;
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
        root = inflater.inflate(R.layout.fragment_remote, container, false);
        ButterKnife.bind(this, root);
        bindViews();
        return root;
    }

    private void bindViews() {
        add.setOnClickListener(this);
        back.setOnClickListener(this);
        blue.setOnClickListener(this);
        down.setOnClickListener(this);
        green.setOnClickListener(this);
        home.setOnClickListener(this);
        info.setOnClickListener(this);
        left.setOnClickListener(this);
        myNow.setOnClickListener(this);
        ok.setOnClickListener(this);
        onDemand.setOnClickListener(this);
        red.setOnClickListener(this);
        right.setOnClickListener(this);
        tvGuide.setOnClickListener(this);
        up.setOnClickListener(this);
        yellow.setOnClickListener(this);
        key_quick_back.setOnClickListener(this);
        key_quick_play.setOnClickListener(this);
        key_play.setOnClickListener(this);
        key_record.setOnClickListener(this);
        key_restart.setOnClickListener(this);
        key_exit.setOnClickListener(this);
        key_now.setOnClickListener(this);
        key_tv.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

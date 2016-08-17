package com.pccw.nowplayer.activity.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.stbCompanion.NMAFSTBCompanion;
import com.pccw.nowtv.nmaf.stbCompanion.NMAFSTBCompanionLegacy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/6/4.
 */
public class ScreenCastActivity extends FrameLayout implements View.OnClickListener {


    Node node;

    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.tv_synopsis)
    TextView tvSynopsis;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_pause_play)
    ImageView ivPausePlay;
    @Bind(R.id.iv_fast)
    ImageView ivFast;
    @Bind(R.id.ll_left_ctrl)
    LinearLayout llLeftCtrl;
    @Bind(R.id.iv_vol_down)
    ImageView ivVolDown;
    @Bind(R.id.iv_vol_up)
    ImageView ivVolUp;
    @Bind(R.id.ll_vol_ctrl)
    LinearLayout llVolCtrl;
    @Bind(R.id.iv_screen_cast)
    ImageView ivScreenCast;
    @Bind(R.id.ll_right_ctrl)
    LinearLayout llRightCtrl;
    @Bind(R.id.ll_controlPanel)
    RelativeLayout llControlPanel;
    @Bind(R.id.fl_screen_cast_root)
    RelativeLayout flScreenCastRoot;


    public ScreenCastActivity(Context context) {
        super(context);
        init();
    }

    public ScreenCastActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public ScreenCastActivity(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_screen_cast, this);
        ButterKnife.bind(this);
        bindEvents();
    }

    public void setNode(Node node) {
        this.node = node;
        fillView(node);
    }

    private void fillView(Node node) {
        if (node.isEPG()) {
            ivCover.setVisibility(View.GONE);
            llLeftCtrl.setVisibility(View.GONE);
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) llVolCtrl.getLayoutParams();
            rllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            ivCover.setVisibility(View.VISIBLE);
            llLeftCtrl.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(node.getImageUrl()).into(new Target() {
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    ivCover.post(new Runnable() {
                        @Override
                        public void run() {
                            ivCover.setImageBitmap(bitmap);
                        }
                    });
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
        }
        tvSynopsis.setText(node.getSynopsis());
    }

    private void bindEvents() {
        ivScreenCast.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivFast.setOnClickListener(this);
        ivPausePlay.setOnClickListener(this);
        ivVolDown.setOnClickListener(this);
        ivVolUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        NMAFSTBCompanionLegacy.Keycode key = null;
        if (v.getId() == R.id.iv_screen_cast) {
            ((VideoPlayer)getContext()).handleScreenCast(false);
        } else if (v.getId() == R.id.iv_back) {
            key = NMAFSTBCompanionLegacy.Keycode.RCFASTREWIND;
        } else if (v.getId() == R.id.iv_fast) {
            key = NMAFSTBCompanionLegacy.Keycode.RCFASTFORWARD;
        } else if (v.getId() == R.id.iv_pause_play) {
            key = NMAFSTBCompanionLegacy.Keycode.RCPLAYPAUSE;
        } else if (v.getId() == R.id.iv_vol_down) {
            key = NMAFSTBCompanionLegacy.Keycode.RCVOLUMEDECREASE;
        } else if (v.getId() == R.id.iv_vol_up) {
            key = NMAFSTBCompanionLegacy.Keycode.RCVOLUMEINCREASE;
        }

        if (key != null) {
            NMAFSTBCompanion.getSharedInstance().sendKeyCode(DeviceManager.getInstance().getConnectDevice().changeToNowDevice(), key.getValue(), new NMAFBaseModule.ErrorCallback() {
                @Override
                public void operationComplete(Throwable throwable) {
                    L.e(throwable);
                }
            });
        }

    }
}

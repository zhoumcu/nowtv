package com.pccw.nowplayer.activity.video;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFMediaPlayerController;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/7/26.
 */
public class TargetVideo extends RelativeLayout implements View.OnClickListener {


    @Bind(R.id.tv_ad_countdown)
    TextView tvAdCountdown;
    @Bind(R.id.tv_ad_title)
    TextView tvAdTitle;
    @Bind(R.id.tv_ad_url)
    TextView tvAdUrl;
    @Bind(R.id.ll_ad_title)
    LinearLayout llAdTitle;
    @Bind(R.id.bt_close_title)
    ImageButton btCloseTitle;
    @Bind(R.id.tv_skip_tips)
    TextView tvSkipTips;
    @Bind(R.id.bt_ad_skip)
    Button btAdSkip;
    @Bind(R.id.rl_skip)
    RelativeLayout rlSkip;

    NMAFMediaPlayerController.PlaylistItem playlistItem;
    VideoPlayer videoPlayer;
    String formatTips;
    String formatAD = "AD:(%d:%02d)";

    public TargetVideo(Context context) {
        super(context);
        init();
    }

    public TargetVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_player_ad, this);
        ButterKnife.bind(this);
        videoPlayer = (VideoPlayer) getContext();
        formatTips = getContext().getString(R.string.ad_tips);
        bindEvent();

    }

    private void bindEvent() {
        btCloseTitle.setOnClickListener(this);
    }


    public void handlePlayListItem(final NMAFMediaPlayerController.PlaylistItem playlistItem) {
        tvAdUrl.setText(playlistItem.getUrl());
        tvAdTitle.setText(playlistItem.getName());
        llAdTitle.setOnClickListener(this);
        tvAdCountdown.setVisibility(View.VISIBLE);
        btAdSkip.setVisibility(View.GONE);

        if (playlistItem.getTargetVideoSkipTime() <= 0) {
            rlSkip.setVisibility(View.GONE);
        } else {
            rlSkip.setVisibility(View.VISIBLE);
            tvSkipTips.setVisibility(View.VISIBLE);
            llAdTitle.setVisibility(View.VISIBLE);
            btCloseTitle.setVisibility(View.VISIBLE);
        }
        removeCallbacks(null);
        post(new Runnable() {
            @Override
            public void run() {
                if (updateAdViews(playlistItem) && getVisibility() == View.VISIBLE) {
                    postDelayed(this, 1000);
                }
            }
        });
        btAdSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_ad_title) {
            if (playlistItem != null) {
                if (!Check.isEmpty(playlistItem.getUrl())) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.ACTION_WEB, playlistItem.getUrl());
                    NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_WEB, bundle);
                }
            }
        } else if (v.getId() == R.id.bt_ad_skip) {
            if (videoPlayer.getMediaPlayer() != null) {
                setVisibility(View.GONE);
                removeCallbacks(null);
                if(videoPlayer.getMediaPlayer()!=null){
                    videoPlayer.getMediaPlayer().playNextItem();
                }
            }
        } else if (v.getId() == R.id.bt_close_title) {
            llAdTitle.setVisibility(View.GONE);
            btCloseTitle.setVisibility(View.GONE);
        }
    }

    /**
     * @param playlistItem
     * @return
     */
    private boolean updateAdViews(NMAFMediaPlayerController.PlaylistItem playlistItem) {

        if (videoPlayer.getMediaPlayer() == null) {
            return false;
        }
        NMAFMediaPlayerController mediaPlayer = videoPlayer.getMediaPlayer();

        long now = videoPlayer.getMediaPlayer().getMovieLength() - mediaPlayer.getCurrentPosition();
        long ellapsed = now / 1000;
        boolean skippable = mediaPlayer.getCurrentPosition() > playlistItem.getTargetVideoSkipTime();
        if (!skippable) {
            long remaining = (playlistItem.getTargetVideoSkipTime() - mediaPlayer.getCurrentPosition()) / 1000;
            tvSkipTips.setText(String.format(formatTips, remaining));
        } else {
            tvSkipTips.setVisibility(View.GONE);
            btAdSkip.setVisibility(View.VISIBLE);
        }

        if (ellapsed > 0) {
            long remaining = Math.max(0, ellapsed);
            int sec = (int) ((remaining) % 60);
            int min = (int) ((remaining) / 60);
            tvAdCountdown.setText(String.format(formatAD, min, sec));
        } else {
            tvAdCountdown.setVisibility(View.INVISIBLE);
            return false;
        }
        return true;
    }
}

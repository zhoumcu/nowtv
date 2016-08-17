package com.pccw.nowplayer.activity.video;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.widget.SmoothSeekBar;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFMediaPlayerController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/7/26.
 */
public class PlayControl extends LinearLayout implements View.OnClickListener {
    private final int PROGRESS_MAX = 10000;
    String formatTime = "%d:%02d";
    @Bind(R.id.iv_minisize)
    ImageView ivMinisize;
    @Bind(R.id.iv_pause_start)
    ImageView ivPauseStart;
    @Bind(R.id.iv_screen_cast)
    ImageView ivScreenCast;
    @Bind(R.id.iv_status)
    ImageView ivStatus;
    Node liveProgram;
    Node node;
    @Bind(R.id.sb_playPosition)
    SmoothSeekBar sbPlayPosition;
    @Bind(R.id.tv_end_time)
    TextView tvEndTime;
    @Bind(R.id.tv_start_time)
    TextView tvStartTime;
    VideoPlayer videoPlayer;
    private boolean isTouchingSeekBar;


    public PlayControl(Context context) {
        super(context);
        init();
    }

    public PlayControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void bindEvent() {
        ivPauseStart.setOnClickListener(this);
        ivMinisize.setOnClickListener(this);
        ivScreenCast.setOnClickListener(this);
        sbPlayPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouchingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouchingSeekBar = false;
                NMAFMediaPlayerController mediaPlayer = videoPlayer.getMediaPlayer();
                if (mediaPlayer != null && mediaPlayer.getMovieLength() != 0) {
                    mediaPlayer.setCurrentPosition((int) (mediaPlayer.getMovieLength() * (sbPlayPosition.getProgress() * 1.0f / 10000)));
                    updateEndTime(mediaPlayer.getCurrentPosition());
                } else {
                    updateEndTime(0);
                }
            }
        });
    }

    private void fillView() {
        if (DeviceManager.getInstance().hasConnectDevice()) {
            ivScreenCast.setEnabled(false);
        } else {
            ivScreenCast.setEnabled(true);
        }
        // load current epg program
        tvStartTime.setVisibility(View.GONE);
        ivPauseStart.setImageResource(R.drawable.ic_pause_selector);
        if (node.isVOD()) {
            tvStartTime.setVisibility(View.GONE);
            ivStatus.setImageResource(R.drawable.ic_forward);
            ivStatus.setOnClickListener(this);
        } else if (node.isEPG()) {
            tvStartTime.setVisibility(View.VISIBLE);
            sbPlayPosition.setVisibility(View.VISIBLE);
            sbPlayPosition.setEnabled(false);
            ivStatus.setImageResource(R.drawable.ic_mode_live);
            ivStatus.setEnabled(false);
            sbPlayPosition.setThumb(null);
            setLiveProgram(node);
        }
        sbPlayPosition.setMax(getSeekBarMaxValue());
        bindEvent();
        updateEndTime(0);
    }

    /**
     * @return total seconds
     */
    public int getSeekBarMaxValue() {
//        if (node.isEPG()) {
//            if (liveProgram != null && liveProgram.getStartTime() != null && liveProgram.getEndTime() != null) {
//                long endTime = liveProgram.getEndTime().getTime();
//                long startTime = liveProgram.getStartTime().getTime();
//                return (int) ((endTime - startTime) / 1000);
//            }
//            return 0;
//        } else {
//            NMAFMediaPlayerController mediaPlayer = videoPlayer.getMediaPlayer();
//            if (mediaPlayer != null) {
//                return mediaPlayer.getMovieLength()/1000;
//            }
//            return 0;
//        }

        return PROGRESS_MAX;
    }

    public void handlePlayListItem(NMAFMediaPlayerController.PlaylistItem playlistItem) {
        sbPlayPosition.setProgress(0);
        final NMAFMediaPlayerController mediaPlayer = videoPlayer.getMediaPlayer();
        if (mediaPlayer == null) {
            return;
        }

        post(new Runnable() {
            @Override
            public void run() {
                if (timeUpdate(mediaPlayer)) {
                    postDelayed(this, 1000);
                }
            }
        });
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_play_control, this);
        ButterKnife.bind(this);
        videoPlayer = (VideoPlayer) getContext();

    }

    public boolean isPlaying() {
        NMAFMediaPlayerController mediaPlayer = videoPlayer.getMediaPlayer();
        return mediaPlayer != null && mediaPlayer.getStatusType() == NMAFMediaPlayerController.StatusType.Playing;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_pause_start) {
            NMAFMediaPlayerController mediaPlayer = videoPlayer.getMediaPlayer();
            if (mediaPlayer != null) {
                if (isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.play();
                }
                updatePlayButton();
            }
        } else if (v.getId() == R.id.iv_status) {
            NMAFMediaPlayerController mediaPlayer = videoPlayer.getMediaPlayer();
            if (mediaPlayer != null) {
                int pos = Math.max(0, mediaPlayer.getCurrentPosition()) - 10 * 1000;
                mediaPlayer.setCurrentPosition(pos);
            }
        } else if (v.getId() == R.id.iv_minisize) {
            videoPlayer.miniSize();
        } else if (v.getId() == R.id.iv_screen_cast) {
            videoPlayer.handleScreenCast(true);
        }

    }

    public void setLiveProgram(Node liveProgram) {
        this.liveProgram = liveProgram;
        Date startTime = liveProgram.getStartTime();
        Date endTime = liveProgram.getEndTime();
        SimpleDateFormat sdformat = new SimpleDateFormat("hh:mm a", Locale.US);
        String startStr = startTime == null ? "" : sdformat.format(startTime);
        String endStr = endTime == null ? "" : sdformat.format(endTime);
        tvStartTime.setText(startStr);
        tvStartTime.setVisibility(TextUtils.isEmpty(startStr) ? View.GONE : View.VISIBLE);
        tvEndTime.setText(endStr);
        tvEndTime.setVisibility(TextUtils.isEmpty(endStr) ? View.GONE : View.VISIBLE);

    }

    public void setNode(Node node) {
        this.node = node;
        fillView();
    }

    /**
     * abstract
     *
     * @return false will stop
     */
    private boolean timeUpdate(NMAFMediaPlayerController mediaPlayer) {
        if (mediaPlayer != null) {
            if (node.isVOD()) {
                int currentTime = mediaPlayer.getCurrentPosition();
                int maxLength = mediaPlayer.getMovieLength();
                float progress = (currentTime * 1.0f) / maxLength;
                progress = PROGRESS_MAX * progress;//eg 5555
                progress = Math.min(PROGRESS_MAX, progress);
                if (!isTouchingSeekBar) sbPlayPosition.setProgress((int) progress);
                updateEndTime(currentTime);
                if (getVisibility() != View.VISIBLE) {
                    return false;
                }
            } else {
                if (liveProgram != null && liveProgram.getStartTime() != null && liveProgram.getEndTime() != null) {
                    long endTime = liveProgram.getEndTime().getTime();
                    long startTime = liveProgram.getStartTime().getTime();
                    long lengthSec = endTime - startTime;//total length eg 18000
                    long current = System.currentTimeMillis();
                    float progress = (current - startTime); // eg 10000
                    progress = (progress * 1.0f) / lengthSec; //eg 0.55555~
                    progress = PROGRESS_MAX * progress;//eg 5555
                    videoPlayer.canLoadCurrentEPGProgram(current, endTime);
                    progress = Math.min(PROGRESS_MAX, progress);
                    if (!isTouchingSeekBar) sbPlayPosition.setProgress((int) progress);
                    if (getVisibility() != View.VISIBLE) {
                        return false;
                    }
                } else {
                    sbPlayPosition.setProgress(0);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean updateEndTime(int inTime) {
        //if epg ,do not need to update the end time
        if (node.isEPG()) {
            return false;
        }
        int time = inTime / 1000;
        int remaining = Math.max(0, time);
        final int sec = remaining % 60;
        final int min = remaining / 60;
        videoPlayer.canNextEpisode(inTime, remaining);
        tvEndTime.setText(String.format(formatTime, min, sec));
        return true;
    }

    public void updatePlayButton() {
        if (isPlaying()) {
            ivPauseStart.setImageResource(R.drawable.ic_pause_selector);
        } else {
            ivPauseStart.setImageResource(R.drawable.ic_play_selector);
        }
    }

}

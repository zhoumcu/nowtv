package com.pccw.nowplayer.activity.video;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.helper.ViewHelper;
import com.pccw.nowplayer.model.CheckoutClient;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.service.ConfigService;
import com.pccw.nowplayer.service.FloatVideo;
import com.pccw.nowplayer.service.MediaPlayerStatusListener;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFMediaPlayerController;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/5/18.
 */
public class VideoPlayer extends BasePlayer implements View.OnClickListener, CheckoutClient.Callback {


    //AUTO HIND
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 5000;
    private static final int UI_ANIMATION_DELAY = 300;
    private static final Handler mHideHandler = new Handler(Looper.getMainLooper());
    @Bind(R.id.fl_main)
    FrameLayout flMain;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            goneOrVisible(true);
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
    };
    @Bind(R.id.fl_root)
    FrameLayout flRoot;
    @Bind(R.id.fl_screen_cast)
    ScreenCastActivity flScreenCast;
    @Bind(R.id.fl_video)
    FrameLayout flVideo;
    @Bind(R.id.fl_video_root)
    FrameLayout flVideoRoot;
    @Bind(R.id.ll_bpl_result)
    BPLResult llBplResult;
    @Bind(R.id.ll_play_control)
    PlayControl llPlayControl;
    @Bind(R.id.rl_next_episode)
    NextEpisode rlNextEpisode;
    @Bind(R.id.rl_target)
    TargetVideo rlTarget;
    @Bind(R.id.tv_now_id)
    TextView tvNowId;
    private final Runnable showWatermarkRunnable = new Runnable() {
        @Override
        public void run() {
            ViewHelper.generateGravity(tvNowId);
            tvNowId.setVisibility(View.VISIBLE);
            mHideHandler.removeCallbacks(showWatermarkRunnable);
            mHideHandler.postDelayed(hideWatermarkRunnable, 5 * 1000);
        }
    };
    private final Runnable hideWatermarkRunnable = new Runnable() {
        @Override
        public void run() {
            tvNowId.setVisibility(View.GONE);
            mHideHandler.removeCallbacks(hideWatermarkRunnable);
            mHideHandler.postDelayed(mShowPart2Runnable, 5 * 60 * 1000);
        }
    };
    private NMAFBasicCheckout.NMAFCheckoutData checkoutData;
    private ConfigService configService;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private boolean mLoadCurrentEPGProgram = false;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideControls();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private NMAFMediaPlayerController mediaPlayer;
    private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
//                Not network
            } else {
                if (mobNetInfo.isConnected() && !configService.isMobileDataEnabled()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    } else {
                        return;
                    }
                    DialogHelper.wifiToMobileNetworkAlert(VideoPlayer.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mediaPlayer != null) {
                                mediaPlayer.play();
                            }
                        }
                    });

                }
            }
        }
    };
    private boolean nextEpisode = false;
    //if only under screen cast mode
    private boolean onlyScreenCast = false;
    private ArrayList<Node> playList;
    private NMAFMediaPlayerController.PlaylistItem playerItem;
    private int playingIndex = 0;
    private boolean screenCasting = false;
    MediaPlayerStatusListener mediaPlayerStatusListener = new MediaPlayerStatusListener() {

        @Override
        public void onPlaybackFinished(NMAFMediaPlayerController.FinishType finishType, Throwable throwable) {
            if (isFinishing()) return;
            if (playNext() == null) {
                DialogHelper.createEndOfProgramDialog(VideoPlayer.this);
            }
        }

        @Override
        public void onPlaybackReady(NMAFMediaPlayerController.PlaylistItem playlistItem) {
            if (isFinishing()) return;
            DialogHelper.createStreamQualityDialog(VideoPlayer.this);
        }

        @Override
        public void onPlaybackStatusChanged(NMAFMediaPlayerController.StatusType oldStatus, NMAFMediaPlayerController.StatusType newStatus, NMAFMediaPlayerController.PlaylistItem playlistItem) {
            if (isFinishing()) return;
            llPlayControl.updatePlayButton();
        }

        @Override
        public void onPlaybackSwitchToNext(NMAFMediaPlayerController.PlaylistItem playlistItem) {
            playerItem = playlistItem;
            updateDisplayModel();
        }
    };

    @Override
    protected void bindEvents() {
        mContentView.setOnClickListener(this);
    }

    public void canLoadCurrentEPGProgram(long current, long endTime) {
        if (current > endTime && !mLoadCurrentEPGProgram) loadCurrentEPGProgram();
    }

    public void canNextEpisode(int len, int rem) {
        if (playerItem != null && len > 0 && rem <= 15 && !nextEpisode && playerItem.getType() == NMAFMediaPlayerController.PlaylistItem.ItemType.Main) {
            Node node = getNextEpisode();
            L.e("next episode");
            if (node != null) {

                rlNextEpisode.setVisibility(View.VISIBLE);
                rlNextEpisode.setNode(node);//// TODO: 2016/7/27
            }
        }
    }

    private void closeFloatVideo() {
        FloatVideo.getInstance(getApplication()).remove();
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void firstStart() {

        rlTarget.setVisibility(View.GONE);
        flScreenCast.setVisibility(View.GONE);
        llBplResult.setVisibility(View.GONE);
        rlNextEpisode.setVisibility(View.GONE);
        llPlayControl.setVisibility(View.GONE);
        ViewHelper.generateWatermarkText(tvNowId);
        tvNowId.setVisibility(View.GONE);
        nextEpisode = false;
        configService = new ConfigService(this);
        mContentView = flVideo;
        startPlay(checkoutData);
    }

    @Override
    protected NMAFMediaPlayerController getController() {
        return mediaPlayer;
    }

    public NMAFMediaPlayerController getMediaPlayer() {
        return mediaPlayer;
    }

    private Node getNextEpisode() {
        Node node = null;
        if (!Check.isEmpty(playList)) {
            node = playList.get(playingIndex);

        }
        return node;
    }

    public void goneNextEpisode() {
        rlNextEpisode.setVisibility(View.GONE);
        nextEpisode = false;
    }

    private void goneOrVisible(boolean mVisible) {
        toolbar.setVisibility(mVisible ? View.VISIBLE : View.GONE);
        flMain.setVisibility(mVisible ? View.VISIBLE : View.GONE);

    }

    /**
     * TODO
     *
     * @param mVisible
     */
    public void handleScreenCast(boolean mVisible) {
        if (mVisible) {
            //fill screen cast  content
            flScreenCast.setNode(node);
            //resume play
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        }
        screenCasting = mVisible;
        updateDisplayModel();
    }

    private void hideControls() {


        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        goneOrVisible(false);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        String checkoutDataStr = getIntent().getStringExtra(Constants.ARG_CHECK_OUT_DATA);
        if (!TextUtils.isEmpty(checkoutDataStr)) {
            checkoutData = NMAFBasicCheckout.NMAFCheckoutData.fromJSON(getIntent().getStringExtra(Constants.ARG_CHECK_OUT_DATA));
        }
        List<ArrayList<Node>> list = (List<ArrayList<Node>>) getIntent().getSerializableExtra(Constants.ARG_NODE_ARRAY);
        if (!Check.isEmpty(list)) {
            playList = list.get(0);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        registerReceiver(connectionChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        setContentView(R.layout.activity_main_video);
        ButterKnife.bind(this);
        closeFloatVideo();
        mContentView = flVideo;
        mVisible = false;
        firstStart();
        if (node.isEPG()) {
            loadCurrentEPGProgram();
        }
    }

    public void loadCurrentEPGProgram() {
        if (node == null || mLoadCurrentEPGProgram) return;
        mLoadCurrentEPGProgram = true;

        EPGClient.getInstance().loadLiveProgram(node).done(new DoneCallback<Node>() {
            @Override
            public void onDone(Node liveProgram) {
                mLoadCurrentEPGProgram = false;
                setLiveProgram(liveProgram);
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                mLoadCurrentEPGProgram = false;
            }
        });
    }

    public void miniSize() {
        if (checkoutData != null) {
            //FloatVideo.getInstance(getApplication()).showFloatVideo(flVideo);
            //finish();
        }
    }

    @Override
    public void onCheckoutFinished(CheckoutClient client) {
        client.detach();
        if (client.isCancelled() || client.getCheckoutData() == null) {
            return;
        }
        startPlay(client.getCheckoutData());
    }

    @Override
    public void onClick(View v) {
        if (v == mContentView) {
            toggle();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionChangeReceiver);
        mediaPlayer = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {

            if (node.isVOD()) {
                mediaPlayer.updateBookmark(new NMAFBaseModule.ErrorCallback() {
                    @Override
                    public void operationComplete(Throwable throwable) {
                        L.e(throwable);
                    }
                });
            }
            mediaPlayer.onActivityPaused(this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.onActivityResumed(this);
        }
    }

    public Node playNext() {
        Node next = getNextEpisode();
        if (next != null) {
            playingIndex = ++playingIndex % playList.size();
            CheckoutClient.create(node, false).setCallback(this).attachTo(this).begin();
            setNode(next);
        }
        goneNextEpisode();
        return next;
    }

    public void setLiveProgram(Node program) {
        liveProgram = program == null ? Node.emptyNode() : program;
        llPlayControl.setLiveProgram(liveProgram);
        updateTitle();
    }

    public void setNode(Node node) {
        this.node = node;
        this.nextEpisode = false;
    }

    @SuppressLint("InlinedApi")
    private void showControls() {
        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mVisible = true;
        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    public void startPlay(final NMAFBasicCheckout.NMAFCheckoutData checkoutData) {
        if (checkoutData == null) {
            return;
        }
        if (checkoutData.canResume()) {
            //show Alert
            DialogHelper.requestResume(this, node.getTitle(), new DialogHelper.inputDialogCallBack() {
                @Override
                public void inputTextCancel(DialogInterface dialog) {
                    mediaPlayer = new NMAFMediaPlayerController(VideoPlayer.this, flVideo, checkoutData, false, mediaPlayerStatusListener);
                    mediaPlayer.setShouldAutoPlay(true);
                    mediaPlayer.setAllowExternalPlayback(false);
                    nextEpisode = true;
                }

                @Override
                public void inputTextConfirm(DialogInterface dialog, String inputText, int which) {
                    boolean resume = false;
                    if (inputText.equals(getString(R.string.last_viewed_scene))) {
                        resume = true;
                    }
                    mediaPlayer = new NMAFMediaPlayerController(VideoPlayer.this, flVideo, checkoutData, resume, mediaPlayerStatusListener);
                    mediaPlayer.setShouldAutoPlay(true);
                    mediaPlayer.setAllowExternalPlayback(false);
                    nextEpisode = true;
                }
            });
        } else {
            mediaPlayer = new NMAFMediaPlayerController(this, flVideo, checkoutData, false, mediaPlayerStatusListener);
            mediaPlayer.setShouldAutoPlay(true);
            mediaPlayer.setAllowExternalPlayback(false);
            nextEpisode = true;
        }
    }

    //  timer gone/visible ui
    private void toggle() {
        if (mVisible) {
            hideControls();
        } else {
            showControls();
        }
    }

    private void updateDisplayModel() {

        rlTarget.setVisibility(View.GONE);
        flScreenCast.setVisibility(View.GONE);
        llBplResult.setVisibility(View.GONE);
        rlNextEpisode.setVisibility(View.GONE);
        llPlayControl.setVisibility(View.GONE);
        boolean playingMainVideo = false;
        boolean playingTargetVideo = false;
        boolean playingSlateVideo = false;
        mHideHandler.removeCallbacks(showWatermarkRunnable);
        mHideHandler.removeCallbacks(hideWatermarkRunnable);
        if (playerItem != null) {
            playingTargetVideo = playerItem.getType() == NMAFMediaPlayerController.PlaylistItem.ItemType.TargetVideo;
            playingSlateVideo = playerItem.getType() == NMAFMediaPlayerController.PlaylistItem.ItemType.Slate;
            playingMainVideo = playerItem.getType() == NMAFMediaPlayerController.PlaylistItem.ItemType.Main;
        }
        boolean isScreenCast = onlyScreenCast || screenCasting;
        if (isScreenCast) {
            flVideoRoot.setVisibility(View.GONE);
            flScreenCast.setVisibility(View.VISIBLE);
            flScreenCast.setNode(node);
            return;
        }
        flVideoRoot.setVisibility(View.VISIBLE);
        flScreenCast.setVisibility(View.GONE);

        if (playingTargetVideo) {
            getMenu().findItem(R.id.menu_subtitle_audio).setVisible(false);
            getMenu().findItem(R.id.menu_add_mynow).setVisible(false);
            title.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);
            rlTarget.setVisibility(View.VISIBLE);
            rlTarget.handlePlayListItem(playerItem);
        } else if (playingSlateVideo) {
            title.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);
            rlTarget.setVisibility(View.GONE);
            //NOTING TO DO
        } else if (playingMainVideo) {
            if (mediaPlayer != null) {
                getMenu().findItem(R.id.menu_subtitle_audio).setVisible(true);
                getMenu().findItem(R.id.menu_add_mynow).setVisible(true);
            }
            title.setVisibility(View.VISIBLE);
            subtitle.setVisibility(View.VISIBLE);
            String nowId = NowIDClient.getInstance().getNowId();
            if (!Check.isEmpty(nowId)) {
                mHideHandler.post(showWatermarkRunnable);
            }
            llPlayControl.setVisibility(View.VISIBLE);
            llPlayControl.setNode(node);
            llPlayControl.handlePlayListItem(playerItem);

        }
    }
}
package com.pccw.nowplayer.activity.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.node.Node;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/7/26.
 */
public class NextEpisode extends RelativeLayout implements View.OnClickListener {
    Node node;
    View nextEpisodeView;
    @Bind(R.id.tv_next_tips)
    TextView tvNextTips;
    @Bind(R.id.tv_next_title)
    TextView tvNextTitle;
    @Bind(R.id.iv_close_next)
    ImageView ivCloseNext;
    @Bind(R.id.ll_next_program)
    RelativeLayout llNextProgram;
    @Bind(R.id.tv_play_now)
    TextView tvPlayNow;


    VideoPlayer videoPlayer ;


    public NextEpisode(Context context) {
        super(context);
        init();

    }

    public NextEpisode(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NextEpisode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        nextEpisodeView = LayoutInflater.from(getContext()).inflate(R.layout.view_next_episode, this);
        ButterKnife.bind(this);
        videoPlayer = (VideoPlayer) getContext();
        bindEvent();
    }

    private void bindEvent() {
        ivCloseNext.setOnClickListener(this);
    }

    public void setNode(Node node) {
        this.node = node;
        fillView(node);
    }

    private void fillView(Node node) {
        tvNextTitle.setText(node.getTitle());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_play_now) {
            videoPlayer.playNext();

        } else if (v.getId() == R.id.iv_close_next) {
            videoPlayer.goneNextEpisode();
        }
    }
}

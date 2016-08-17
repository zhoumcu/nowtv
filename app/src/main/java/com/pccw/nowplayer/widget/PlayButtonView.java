package com.pccw.nowplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.DownloadStatusTracker;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.DrawableUtils;
import com.pccw.nowplayer.utils.TypeUtils;

/**
 * Created by swifty on 12/8/16.
 */
public class PlayButtonView extends DownloadStatusTrackerView {
    private ImageView imageView;
    private Node node;

    public PlayButtonView(Context context) {
        super(context);
        initView();
    }

    public PlayButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PlayButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ImageView getImage() {
        return imageView;
    }

    private void initView() {
        imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView.setPadding(TypeUtils.dpToPx(getContext(), 5), TypeUtils.dpToPx(getContext(), 5), TypeUtils.dpToPx(getContext(), 5), TypeUtils.dpToPx(getContext(), 5));
        imageView.setImageResource(R.drawable.ic_play);
        addView(imageView);
    }

    @Override
    public void onDownloadStatusChange(DownloadStatusTracker tracker) {
        updatePlayButtonUI();
    }

    public void setImageResource(int ic_tv_play) {
        imageView.setImageResource(ic_tv_play);
    }

    public void setNode(Node node) {
        if (node == null) return;
        this.node = node;
        updatePlayButtonUI();
        bindWithDownloadStatus(node.getDownloadTracker());
    }

    private void updatePlayButtonUI() {
        if (node == null) setVisibility(GONE);
        if (node.isPlayEnabled()) {
            setClickable(true);
            DrawableUtils.setImageTinting(imageView, R.drawable.ic_play, getResources().getColor(R.color.white));
        } else {
            setClickable(false);
            DrawableUtils.setImageTinting(imageView, R.drawable.ic_play, getResources().getColor(R.color.gray));
        }
    }
}

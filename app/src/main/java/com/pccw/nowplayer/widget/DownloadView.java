package com.pccw.nowplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.DownloadStatusTracker;
import com.pccw.nowplayer.utils.DrawableUtils;

/**
 * Created by Swifty on 5/5/2016.
 */
public class DownloadView extends DownloadStatusTrackerView {
    public int progress;
    private ImageView image;
    private ProgressBar progressBar;

    public DownloadView(Context context) {
        super(context);
        init();
    }

    public DownloadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DownloadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void bindWithDownloadStatus(DownloadStatusTracker statusTracker) {
        super.bindWithDownloadStatus(statusTracker);
        updateDownloadUI(statusTracker);
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_download, this, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        image = (ImageView) view.findViewById(R.id.image);
        addView(view);
    }

    @Override
    public void onDownloadStatusChange(DownloadStatusTracker tracker) {
        updateDownloadUI(tracker);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setDisabled() {
        setClickable(false);
        progressBar.setEnabled(false);
        DrawableUtils.setImageTinting(image, R.drawable.ic_download, getContext().getResources().getColor(R.color.gray));
    }

    public void setFinished() {
        setClickable(false);
        progressBar.setEnabled(true);
        progressBar.setProgress(100);
        DrawableUtils.setImageTinting(image, R.drawable.ic_download, getContext().getResources().getColor(R.color.orange));
    }

    public void setPaused() {
        setClickable(true);
        progressBar.setEnabled(true);
        if (progress != 100)
            image.setImageResource(R.drawable.ic_download);
    }

    public void setProgress(int progress) {
        setClickable(true);
        progressBar.setEnabled(true);
        this.progress = progress;
        if (progress < 0) progress = 0;
        else if (progress > 100) progress = 100;
        progressBar.setProgress(progress);
        if (progress == 100) {
            DrawableUtils.setImageTinting(image, R.drawable.ic_download, getContext().getResources().getColor(R.color.orange));
        } else {
            image.setImageResource(R.drawable.ic_pause_dark);
        }
    }

    public void setQueued() {
        progressBar.setEnabled(false);
        setPaused();
    }

    private void updateDownloadUI(DownloadStatusTracker tracker) {
        if (tracker == null) return;
        switch (tracker.getStatus()) {
            case Busy:
                setDisabled();
            case Queued:
                setQueued();
            case InProgress:
                setProgress(tracker.getProgress());
            case Paused:
                setPaused();
            case Completed:
                setFinished();
            case Failed:
                setPaused();
        }
    }

}

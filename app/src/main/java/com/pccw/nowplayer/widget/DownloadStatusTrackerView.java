package com.pccw.nowplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.pccw.nowplayer.model.DownloadStatusTracker;

/**
 * Created by swifty on 12/8/16.
 */
public abstract class DownloadStatusTrackerView extends FrameLayout implements DownloadStatusTracker.Listener {

    private DownloadStatusTracker statusTacker;

    public DownloadStatusTrackerView(Context context) {
        super(context);
    }

    public DownloadStatusTrackerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownloadStatusTrackerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindWithDownloadStatus(DownloadStatusTracker statusTracker) {
        this.statusTacker = statusTracker;
        statusTracker.addListener(this);
    }

    public void unbindWithDownloadStatus() {
        this.statusTacker.removeListener(this);
    }
}

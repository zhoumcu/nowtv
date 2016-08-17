package com.pccw.nowplayer.model;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFStreamDownloader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kriz on 9/8/2016.
 * <p/>
 * Warning: instances of this class are supposed to be singletons. Do not implement Serializable. Do not try to instantiate this class.
 */
public class DownloadStatusTracker {

    private transient NMAFStreamDownloader.NMAFStreamDownloadItem downloadItem;
    private long downloadSize;
    private String identifier;
    private List<Listener> listeners;
    private boolean mGettingProgress;
    private int progress;
    private DownloadStatus status;
    private String statusText;

    DownloadStatusTracker(DownloadClient client) {
        // only DownloadClient should instantiate this class
    }

    public void addListener(Listener listener) {
        if (listener == null) return;
        if (listeners == null) listeners = new LinkedList<>();
        listeners.add(listener);
    }

    public void beginTransition() {
        setStatus(DownloadStatus.Busy);
        informListeners();
    }

    public NMAFStreamDownloader.NMAFStreamDownloadItem getDownloadItem() {
        return downloadItem;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
        updateStatusText();
        informListeners();
    }

    public long getDownloadedSize() {
        double progress = getProgress() / 100.0;
        long size = downloadSize;
        long ret = (long) ((double) size * progress);
        return ret;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        updateStatusText();
        informListeners();
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
        updateStatusText();
        informListeners();
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
        informListeners();
    }

    protected void informListeners() {
        if (listeners == null) return;

        ArrayList<Listener> copy = new ArrayList<>(listeners);
        for (Listener l : copy) {
            l.onDownloadStatusChange(this);
        }
    }

    public void removeAllListeners() {
        if (listeners == null) return;
        listeners.clear();
    }

    public void removeListener(Listener listener) {
        if (listener == null || listeners == null) return;
        listeners.remove(listener);
    }

    public void setDownloadItem(NMAFStreamDownloader.NMAFStreamDownloadItem item, boolean isAdded) {
        this.downloadItem = item;
        if (identifier == null) {
            setIdentifier(item.identifier);
        }
        if (item != null) {
            if (item.completionFlag) {
                setStatus(DownloadStatus.Completed);
                setProgress(100);
            } else if (item.errorFlag) {
                setStatus(DownloadStatus.Failed);
                if (item.progress > 0) setProgress(item.progress);
            } else if (item.pauseFlag) {
                setStatus(DownloadStatus.Paused);
                if (item.progress > 0) setProgress(item.progress);
            } else {
                setStatus(DownloadStatus.InProgress);
                if (item.progress > 0) setProgress(item.progress);
            }
        } else if (isAdded) {
            setStatus(DownloadStatus.Queued);
            setProgress(0);
        } else {
            setStatus(null);
            setProgress(0);
        }
    }

    public void updateProgress() {
        if (status == null) {
            setProgress(0);
        } else switch (status) {
            case Queued:
                setProgress(0);
                break;

            case InProgress:
            case Paused: {
                if (!mGettingProgress) {
                    mGettingProgress = true;
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            int p = NMAFStreamDownloader.getSharedInstance().getDownloadStatus(downloadItem);
                            if (p < DownloadStatusTracker.this.progress) {
                            } else {
                                setProgress(p);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            mGettingProgress = false;
                        }
                    }.execute();
                }
                break;
            }
            case Failed:
                setProgress(0);
                break;

            case Completed:
                setProgress(100);
                break;

            default:
                setProgress(0);
                break;
        }
    }

    public void updateStatusText() {
        String text = null;

        Resources res = PlayerApplication.getContext().getResources();

        if (status == null) {
            // status unknown
        } else switch (status) {

            case InProgress: {
                String downloaded = StringUtils.getByteSizeDescription(getDownloadedSize(), 0);
                String total = StringUtils.getByteSizeDescription(downloadSize, 0);
                text = res.getString(R.string.download_progress_description, downloaded, total);
                break;
            }

            case Queued:
                text = res.getString(R.string.waiting_to_download);
                break;

            case Paused:
            case Failed:
            case Completed:
            default:
                break; // no status text
        }
        setStatusText(text);
    }

    public enum DownloadStatus {
        Busy, Queued, InProgress, Paused, Completed, Failed
    }

    public interface Listener {
        void onDownloadStatusChange(DownloadStatusTracker tracker);
    }
}

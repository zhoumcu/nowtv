package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pccw.nowplayer.helper.ViewHelper;
import com.pccw.nowplayer.model.DownloadClient;
import com.pccw.nowplayer.model.DownloadStatusTracker;
import com.pccw.nowplayer.model.node.Node;

/**
 * Created by Swifty on 7/21/2016.
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected void exceDownloadAction(View itemView, Node node) {
        if (itemView == null | node == null) return;
        switch (node.getDownloadTracker().getStatus()) {
            case Busy:
                break;
            case Queued:
                DownloadClient.getInstance().resumeDownload(node);
                break;
            case InProgress:
                DownloadClient.getInstance().pause(node);
                break;
            case Paused:
                DownloadClient.getInstance().resumeDownload(node);
                break;
            case Completed:
                break;
            case Failed:
                if (itemView.getContext() instanceof FragmentActivity) {
                    DownloadClient.getInstance().addToDownloadList((FragmentActivity) itemView.getContext(), node);
                }
                break;
        }

    }

    protected boolean exceNodeAction(final View view, final Node node) {
        if (view == null || node == null) return false;
        if (node.isPVR()) return false;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHelper.exceNodeAction(view.getContext(), node);
            }
        });
        return true;
    }
}

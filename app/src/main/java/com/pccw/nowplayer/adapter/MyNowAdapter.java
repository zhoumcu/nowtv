package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.DrawableUtils;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.UIUtils;
import com.pccw.nowplayer.widget.DownloadView;
import com.pccw.nowplayer.widget.PlayButtonView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 7/21/2016.
 */
public class MyNowAdapter extends BaseAdapter<MyNowAdapter.ItemHolder> {

    private final Context context;
    private final List<Node> list;


    public MyNowAdapter(Context context, List<Node> list) {
        if (list == null) list = new ArrayList<>();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        final Node node = list.get(position);
        UIUtils.setText(holder.title, node.getTitle(), true);
        UIUtils.setText(holder.subtitle, node.getLibraryName(), true);
        UIUtils.setText(holder.time, node.getStatusText(), true);
        ImageUtils.loadImage(holder.image, node, false);
        if (!exceNodeAction(holder.itemView, node)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (node.isSeries()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.ARG_NODE, node);
                        NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_EPISODE_LISTING, bundle);
                    }
                }
            });
        }
        showActionView(node, holder);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.view_watch_list_item, parent, false));
    }

    @Override
    public void onViewDetachedFromWindow(ItemHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.download.unbindWithDownloadStatus();
    }

    private void showActionView(final Node node, final ItemHolder holder) {
        if (node.isPVR()) {
            holder.play.setVisibility(View.GONE);
            holder.download.setVisibility(View.GONE);
            Object failed = node.getAttribute("pvrFailed");
            if (node.isSeries()) {
                holder.actionText.setVisibility(View.GONE);
                holder.arrow.setVisibility(View.VISIBLE);
            } else {
                if (failed instanceof Boolean) {
                    holder.arrow.setVisibility(View.GONE);
                    holder.actionText.setVisibility(View.VISIBLE);
                    if ((Boolean) failed) {
                        holder.actionText.setText(context.getString(R.string.failed));
                        holder.actionText.setTextColor(context.getResources().getColor(R.color.gray));
                    } else {
                        holder.actionText.setText(context.getString(R.string.scheduled));
                        holder.actionText.setTextColor(context.getResources().getColor(R.color.orange));
                    }
                }
            }
        } else {
            holder.arrow.setVisibility(View.GONE);
            holder.actionText.setVisibility(View.GONE);
            if (node.isPlayEnabled()) {
                holder.play.setNode(node);
                holder.play.setVisibility(View.VISIBLE);
                if (node.isPlayable()) {
                    holder.play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.ARG_NODE, node);
                            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_PLAYER, bundle);
                        }
                    });
                } else {
                    DrawableUtils.setImageTinting(holder.play.getImage(), R.drawable.ic_play, context.getResources().getColor(R.color.gray));
                    holder.play.setClickable(false);
                }
            } else if (node.canScreencastOnly()) {
                holder.play.setImageResource(R.drawable.ic_tv_play);
                holder.play.setVisibility(View.VISIBLE);
                holder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.ARG_NODE, node);
                        NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_SCREEN_CAST, bundle);
                    }
                });
            } else {
                holder.play.setVisibility(View.GONE);
            }
            if (node.isDownloadable()) {
                holder.download.bindWithDownloadStatus(node.getDownloadTracker());
                holder.download.setVisibility(View.VISIBLE);
                holder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exceDownloadAction(holder.itemView, node);
                    }
                });
            } else {
                holder.download.setVisibility(View.GONE);
                holder.play.unbindWithDownloadStatus();
            }
        }

    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.action_text)
        TextView actionText;
        @Bind(R.id.arrow)
        ImageView arrow;
        @Bind(R.id.download)
        DownloadView download;
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.play)
        PlayButtonView play;
        @Bind(R.id.subtitle)
        TextView subtitle;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.title)
        TextView title;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

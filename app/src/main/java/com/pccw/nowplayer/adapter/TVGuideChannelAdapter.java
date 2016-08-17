package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.constant.VideoTypeIndex;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/14/2016.
 */
public class TVGuideChannelAdapter extends RecyclerView.Adapter<TVGuideChannelAdapter.ViewHolder> {

    private final List<Node> channelList;
    private final Context context;
    private Set<Integer> lastVisibleChannelIds;
    private LinearLayoutManager layoutManager;
    private long timestamp;

    public TVGuideChannelAdapter(Context context, LinearLayoutManager layoutManager, List<Node> channelList, long timestamp) {
        this.context = context;
        this.layoutManager = layoutManager;
        this.channelList = channelList;
        this.timestamp = timestamp;

        if (channelList != null) for (Node ch : channelList) {
            ch.findPlayingProgramsAt(timestamp);
            ch.setHasMore(true);
        }
    }

    private void changeStyle(ViewHolder holder, boolean isSubscribed) {
        if (!NowIDClient.getInstance().isLoggedIn() || isSubscribed) {
            ImageUtils.changeImageSaturation(holder.channelIcon, 1);
            holder.start0.setTextColor(context.getResources().getColor(R.color.white));
            holder.title0.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            ImageUtils.changeImageSaturation(holder.channelIcon, 0);
            holder.start0.setTextColor(context.getResources().getColor(R.color.now_grey));
            holder.title0.setTextColor(context.getResources().getColor(R.color.now_grey));
        }
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public List<Node> getVisibleChannels() {
        if (layoutManager == null || channelList == null) return new ArrayList<>();
        int s = layoutManager.findFirstVisibleItemPosition();
        int e = layoutManager.findLastVisibleItemPosition();
        ArrayList<Node> ret = new ArrayList<>();
        for (int i = s; i <= e; i++) {
            if (i >= 0 && i < channelList.size()) ret.add(channelList.get(i));
        }
        return ret;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Node channel = channelList.get(position);

        holder.channelId.setText(channel.getChannelCode());
        changeStyle(holder, channel.isSubscribed());
        ImageUtils.loadChannelImage(holder.channelIcon, channel.getChannelId());
        boolean empty = channel.currentPlayingProgram == null && channel.nextPlayingProgram == null;
        holder.no_info_text.setVisibility(empty && !channel.isLoading() ? View.VISIBLE : View.GONE);
        holder.progress.setVisibility(channel.isLoading() ? View.VISIBLE : View.GONE);

        resetAllAsynData(holder);
        if (channel.currentPlayingProgram != null) {
            holder.start0.setText(channel.currentPlayingProgram.getStartTimeText());
            holder.title0.setText(channel.currentPlayingProgram.getTitle());
            holder.title0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.ARG_CHANNEL, channel);
                    bundle.putSerializable(Constants.ARG_NODE, channel.currentPlayingProgram);
                    NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.EPG, bundle);
                }
            });
            showIcon(holder.ic0, channel.currentPlayingProgram);
        }
        if (channel.nextPlayingProgram != null) {
            holder.start1.setText(channel.nextPlayingProgram.getStartTimeText());
            holder.title1.setText(channel.nextPlayingProgram.getTitle());
            holder.title1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.ARG_CHANNEL, channel);
                    bundle.putSerializable(Constants.ARG_NODE, channel.nextPlayingProgram);
                    NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.EPG, bundle);
                }
            });
            showIcon(holder.ic1, channel.nextPlayingProgram);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.ARG_NODE, channel);
                NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_TV_GUIDE_CHANNEL_DETAIL, bundle);
            }
        });
        showIcon(holder.like, channel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_tv_guide_list_item, parent, false));
    }

    private void resetAllAsynData(ViewHolder holder) {
        holder.start1.setText(null);
        holder.title1.setText(null);
        holder.start0.setText(null);
        holder.title0.setText(null);
        holder.ic0.setImageResource(0);
        holder.ic1.setImageResource(0);
        holder.like.setImageResource(0);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        lastVisibleChannelIds = null;
        updateSchedule();
    }

    private void showIcon(ImageView imageView, Node node) {
        if (node.isFavorite()) {
            imageView.setImageResource(R.drawable.ic_heart);
        } else if (node.isInWatchList()) {
            imageView.setImageResource(R.drawable.epg_in_watchlist);
        } else if (node.isRecommended()) {
            imageView.setImageResource(R.drawable.ic_start);
        } else {
            imageView.setImageResource(0);
        }
    }

    public void updateChannelCell(Node channel) {
        int pos = channelList.indexOf(channel);
        if (pos != -1) notifyItemChanged(pos);
    }

    public void updateChannelCells(List<Node> channels) {
        if (channels == null) return;
        for (Node ch : channels) {
            updateChannelCell(ch);
        }
    }

    public void updateSchedule() {
        final List<Node> visibleChannels = getVisibleChannels();
        final List<Node> channelsToLoad = new ArrayList<>();
        final List<Node> channelsToUpdate = new ArrayList<>();
        final Set<Integer> visibleIds = new HashSet<>();

        // refresh all channel's current playing program
        if (channelList != null) for (Node ch : channelList) {
            ch.findPlayingProgramsAt(timestamp);
        }

        for (Node ch : visibleChannels) {

            visibleIds.add(ch.getChannelId());
            channelsToUpdate.add(ch);

            if (ch.hasMore()) {
                ch.setLoading(true);
                channelsToLoad.add(ch);
            }
        }

        if (lastVisibleChannelIds != null && lastVisibleChannelIds.containsAll(visibleIds)) {
            // nothing to update
        } else {
            lastVisibleChannelIds = visibleIds;

            if (channelsToLoad.size() > 0) {
                EPGClient.getInstance().loadPrograms(channelsToLoad, 0, 2).then(new DoneCallback<List<Node>>() {
                    @Override
                    public void onDone(List<Node> result) {
                        for (Node ch : channelsToLoad) {
                            ch.findPlayingProgramsAt(timestamp);
                            ch.setLoading(false);
                            ch.setHasMore(false);
                            updateChannelCell(ch);
                        }
                    }
                }).fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        for (Node ch : channelsToLoad) {
                            ch.setLoading(false);
                            updateChannelCell(ch);
                        }
                    }
                });
            }
        }

        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.channel_icon)
        ImageView channelIcon;
        @Bind(R.id.channel_id)
        TextView channelId;
        @Bind(R.id.ic_0)
        ImageView ic0;
        @Bind(R.id.ic_1)
        ImageView ic1;
        @Bind(R.id.like)
        ImageView like;
        @Bind(R.id.no_info_text)
        View no_info_text;
        @Bind(R.id.progress)
        ProgressBar progress;
        @Bind(R.id.start_0)
        TextView start0;
        @Bind(R.id.start_1)
        TextView start1;
        @Bind(R.id.title_0)
        TextView title0;
        @Bind(R.id.title_1)
        TextView title1;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

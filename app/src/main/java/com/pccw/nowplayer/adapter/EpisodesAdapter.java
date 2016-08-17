package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.UIUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/3/2016.
 */
public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder> {


    List<Node> nodes;

    private Context context;

    public EpisodesAdapter(Context context, List<Node> nodes) {
        this.nodes = nodes;
        this.context = context;
    }

    private String getConvertDate(String endDateText) {
        String dateStr = null;
        try {
            DateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sourceFormat.parse(endDateText);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            dateStr = df.format(date);
            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return nodes == null ? 0 : nodes.size();
    }

    @Override
    public void onBindViewHolder(final EpisodesViewHolder holder, int position) {

        final Node node = nodes.get(position);

        UIUtils.setText(holder.tvEp, "EP" + node.getEpisodeNum(), true);
        UIUtils.setText(holder.tvTitle, node.getTitle(), true);
        UIUtils.setText(holder.tvEndTime, "Ends " + getConvertDate(node.getEndDateText()), true);
        holder.ivDownload.setVisibility(node.isDownloadable() ? View.VISIBLE : View.GONE);
        holder.ivPlay.setVisibility(node.isPlayable() ? View.VISIBLE : View.GONE);
        holder.ivScreenCast.setVisibility(node.canScreencastOnly() ? View.VISIBLE : View.GONE);
        holder.ivScreenCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.ARG_NODE, node);
                NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_SCREEN_CAST, bundle);
            }
        });
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.ARG_NODE, node);
                NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_PLAYER, bundle);
            }
        });
    }

    @Override
    public EpisodesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EpisodesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_episodes, parent, false));
    }

    class EpisodesViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_ep)
        TextView tvEp;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_end_time)
        TextView tvEndTime;
        @Bind(R.id.ll_center)
        RelativeLayout llCenter;
        @Bind(R.id.iv_download)
        ImageView ivDownload;
        @Bind(R.id.iv_screen_cast)
        ImageView ivScreenCast;
        @Bind(R.id.iv_play)
        ImageView ivPlay;
        @Bind(R.id.rl_actions)
        LinearLayout rlActions;

        public EpisodesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}

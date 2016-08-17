package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.constant.VideoTypeIndex;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/3/2016.
 */
public class OtherTimesAdapter extends RecyclerView.Adapter<OtherTimesAdapter.OtherTimeViewHolder> {


    Node channel;
    Date day7;
    SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
    int lastDay;
    List<Node> nodes;
    List<Node> titleNode = new ArrayList<>();
    Date today;
    Date tomorrow;
    private Context context;


    public OtherTimesAdapter(Context context, List<Node> nodes, Node channel) {
        this.nodes = nodes;
        this.context = context;
        this.channel = channel;
        today = setDay(0);
        tomorrow = setDay(1);
        day7 = setDay(7);
    }

    private String format(Date date) {

        String dateStr = df.format(date);
        if (date.before(today)) {
            return String.format(context.getString(R.string.today), dateStr);
        } else if (date.before(tomorrow)) {
            return String.format(context.getString(R.string.tomorrow), dateStr);
        } else if (date.before(day7)) {
            SimpleDateFormat df = new SimpleDateFormat("EEE", Locale.getDefault());
            return df.format(date) + "  " + dateStr;
        } else {
            return dateStr;
        }

    }

    @Override
    public int getItemCount() {
        return nodes == null ? 0 : nodes.size();
    }

    @Override
    public void onBindViewHolder(final OtherTimeViewHolder holder, int position) {
        final Node node = nodes.get(position);

        Date date = node.getStartTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.YEAR) * (calendar.get(Calendar.MONTH) + 1) * 32 * calendar.get(Calendar.DAY_OF_MONTH);
        if (day > lastDay || titleNode.contains(node)) {
            if (!titleNode.contains(node)) {
                titleNode.add(node);
            }
            UIUtils.setText(holder.tvDay, format(node.getStartTime()), true);
        } else {
            holder.tvDay.setVisibility(View.GONE);
        }
        lastDay = day;
        UIUtils.setText(holder.tvTime, node.getStartTimeText(), true);
        UIUtils.setText(holder.tvTitle, node.getChannelCode(), true);
        ImageUtils.loadChannelImage(holder.ivChannelLogo, node.getChannelId());
        holder.other_times_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.ARG_CHANNEL, channel);
                bundle.putSerializable(Constants.ARG_NODE, node);
                NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.EPG, bundle);
            }
        });
    }

    @Override
    public OtherTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OtherTimeViewHolder(LayoutInflater.from(context).inflate(R.layout.view_othertime, null, false));
    }

    private Date setDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return new Date(calendar.getTimeInMillis());
    }

    class OtherTimeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_channel_logo)
        ImageView ivChannelLogo;
        @Bind(R.id.other_times_item)
        View other_times_item;
        @Bind(R.id.tv_day)
        @Nullable
        TextView tvDay;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_title)
        TextView tvTitle;

        public OtherTimeViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }
}

package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swifty on 5/17/2016.
 */
public class NPXNodeAdapter extends BaseAdapter<NPXNodeAdapter.ViewHolder> {

    private final int MAXCOUNT;
    private final boolean sameSizeItem;
    private Context context;
    List<Node> nodes;

    public NPXNodeAdapter(Context context, List<Node> nodes, int MAX) {
        this(context, nodes, MAX, false);
    }

    public NPXNodeAdapter(Context context, List<Node> nodes, boolean sameSizeItem) {
        this(context, nodes, -1, sameSizeItem);
    }

    public NPXNodeAdapter(Context context, List<Node> nodes) {
        this(context, nodes, -1);
    }

    public NPXNodeAdapter(Context context, List<Node> nodes, int MAX, boolean sameSizeItem) {
        if (nodes == null) nodes = new ArrayList<>();
        this.nodes = nodes;
        this.context = context;
        this.MAXCOUNT = MAX;
        this.sameSizeItem = sameSizeItem;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (sameSizeItem) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_horizontal_list_item, parent, false));
        } else {
            if (NodeType.isType(viewType, NodeType.ChannelItemStyle)) {
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_cat3_item, parent, false));
            } else {
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_horizontal_list_item, parent, false));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (int) nodes.get(position).getType();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Node node = nodes.get(position);
        ImageUtils.loadImage(holder.image, node);
        UIUtils.setHtml(holder.title, node.getTitle(), true);
        UIUtils.setHtml(holder.subtitle, node.getStatusText(), true);
        exceNodeAction(holder.itemView, node);
    }

    @Override
    public int getItemCount() {
        if (MAXCOUNT < 0) return nodes.size();
        return nodes.size() > MAXCOUNT ? MAXCOUNT : nodes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}
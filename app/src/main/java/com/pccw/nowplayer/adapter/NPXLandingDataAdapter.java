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
import com.pccw.nowplayer.constant.VideoTypeIndex;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;

import java.util.List;

/**
 * Created by Swifty on 5/3/2016.
 * has been deprecated because all data model use node wrapper
 */
@Deprecated
public class NPXLandingDataAdapter extends RecyclerView.Adapter<NPXLandingDataAdapter.NPXLandingDataHolder> {

    private final int MAXCOUNT;
    List<Node> nodes;
    private Context context;

    public NPXLandingDataAdapter(Context context, List<Node> nodes, int MAXCOUNT) {
        this.nodes = nodes;
        this.context = context;
        this.MAXCOUNT = MAXCOUNT;
    }

    public NPXLandingDataAdapter(Context context, List<Node> nodes) {
        this(context, nodes, -1);
    }

    @Override
    public int getItemCount() {
        return nodes == null ? 0 : (nodes.size() > MAXCOUNT ? MAXCOUNT : nodes.size());
    }

    @Override
    public void onBindViewHolder(NPXLandingDataHolder holder, int position) {
        final Node node = nodes.get(position);
        ImageUtils.loadImage(holder.image, node.getImageUrl(), R.drawable.placeholder);
        holder.title.setText(node.getTitle());
        holder.subtitle.setText(node.getSubtitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.ARG_NODE, node);
                NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_DETAIL + ":" + (node.isEPG() ? VideoTypeIndex.EPG : VideoTypeIndex.VOD), bundle);
            }
        });
    }

    @Override
    public NPXLandingDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NPXLandingDataHolder(LayoutInflater.from(context).inflate(R.layout.view_horizontal_list_item, parent, false));
    }

    class NPXLandingDataHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView subtitle;
        TextView title;

        public NPXLandingDataHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}

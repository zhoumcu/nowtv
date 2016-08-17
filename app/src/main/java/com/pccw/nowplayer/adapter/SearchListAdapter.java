package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.Group;
import com.pccw.nowplayer.model.SearchTable;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.ViewUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

import java.util.List;
import java.util.Map;

/**
 * Created by Swifty on 5/3/2016.
 */
public class SearchListAdapter extends MapListAdapter<Group, Object> {

    private final boolean showImagePlace;

    public interface SearchListener {
        void onSearch(String query);

        void onDelete(int pos, SearchTable id);

        void onDeleteAll();
    }

    public static final int TYPE_CHANNEL = 2;
    public static final int TYPE_PROGRAM = 3;
    public static final int TYPE_HISTORY = 6;
    public static final int TYPE_HISTORY_GROUP = 7;
    public static final int TYPE_TOP_SEARCH = 8;
    SearchListener searchListener;

    public void setSearchListener(SearchListener searchListener) {
        this.searchListener = searchListener;
    }

    public SearchListAdapter(Map<Group, List<Object>> stringListMap, Context context) {
        this(stringListMap, context, false);
    }

    public SearchListAdapter(Map<Group, List<Object>> stringListMap, Context context, boolean showImagePlace) {
        super(stringListMap, context);
        this.showImagePlace = showImagePlace;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_GROUP:
                return new GroupItemHolder(LayoutInflater.from(context).inflate(R.layout.view_search_group_item, parent, false));
            case TYPE_CHANNEL:
                return new ChannelItemHolder(LayoutInflater.from(context).inflate(R.layout.view_search_channel_item, parent, false));
            case TYPE_PROGRAM:
                return new ProgramItemHolder(LayoutInflater.from(context).inflate(R.layout.view_search_cell_item, parent, false));
            case TYPE_HISTORY:
                return new HistoryItemHolder(LayoutInflater.from(context).inflate(R.layout.view_search_history_item, parent, false));
            case TYPE_TOP_SEARCH:
                return new TopGroupItemHolder(LayoutInflater.from(context).inflate(R.layout.view_search_top_group_item, parent, false));
            case TYPE_HISTORY_GROUP:
                return new GroupHistoryItemHolder(LayoutInflater.from(context).inflate(R.layout.view_search_history_group_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupItemHolder) {

            Group group = findGroupCellByPos(position);
            getGroupView((GroupItemHolder) holder, group);

        } else if (holder instanceof GroupHistoryItemHolder) {

            Group group = (Group) findGroupCellByPos(position);
            getHistoryGroupView((GroupHistoryItemHolder) holder, group);

        } else if (holder instanceof ChannelItemHolder) {

            List<Node> channels = (List<Node>) findCellItemByPos(position);
            getChannelView((ChannelItemHolder) holder, channels);

        } else if (holder instanceof ProgramItemHolder) {

            Node program = (Node) findCellItemByPos(position);
            getProgramView((ProgramItemHolder) holder, program);

        } else if (holder instanceof HistoryItemHolder) {

            SearchTable searchTable = (SearchTable) findCellItemByPos(position);
            getHistoryView((HistoryItemHolder) holder, searchTable, position);

        } else if (holder instanceof TopGroupItemHolder) {

            DataModels.NPXEpgTopSearchProgramModel topSearchProgramModel = (DataModels.NPXEpgTopSearchProgramModel) findCellItemByPos(position);
            getTopSearchView((TopGroupItemHolder) holder, topSearchProgramModel);
        }
    }

    private void getTopSearchView(TopGroupItemHolder holder, final DataModels.NPXEpgTopSearchProgramModel topSearchProgramModel) {
        holder.title.setText(topSearchProgramModel.common_brand_name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchListener != null) {
                    searchListener.onSearch(topSearchProgramModel.common_brand_name);
                }
            }
        });
    }

    private void getHistoryGroupView(GroupHistoryItemHolder holder, Group historyGroup) {
        holder.title.setText(historyGroup.title);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchListener != null) {
                    searchListener.onDeleteAll();
                }
            }
        });
    }

    private void getHistoryView(final HistoryItemHolder holder, final SearchTable searchTable, final int pos) {
        holder.title.setText(searchTable.search_value);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchListener != null) {
                    searchListener.onSearch(searchTable.search_value);
                }
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchListener != null) {
                    searchListener.onDelete(pos, searchTable);
                }
            }
        });
    }

    private void getProgramView(ProgramItemHolder holder, Node program) {
        ImageUtils.loadImage(holder.image, program, !showImagePlace);
        ViewUtils.setTextIfEmptyGone(holder.title, program.getTitle());
        ViewUtils.setTextIfEmptyGone(holder.subtitle, program.getLibraryName());
        exceNodeAction(holder.itemView, program);
    }

    private void getChannelView(ChannelItemHolder holder, List<Node> channels) {
        holder.container.removeAllViews();
        for (final Node channel : channels) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_channel_item, holder.container, false);
            final ImageView imageView = (ImageView) view.findViewById(R.id.image);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            ViewUtils.setTextIfEmptyGone(title, channel.getTitle());
            ViewUtils.setTextIfEmptyGone(subtitle, null);
            ImageUtils.loadChannelImage(imageView, channel.getChannelId());
            exceNodeAction(view, channel);
            holder.container.addView(view);
        }
    }

    private void getGroupView(GroupItemHolder holder, Group group) {
        holder.title.setText(group.title);
    }


    @Override
    public int getItemViewType(int position) {
        if (findTypeByPos(position) == TYPE_GROUP) {
            if ("History".equals(findGroupCellByPos(position).title)) return TYPE_HISTORY_GROUP;
            else return TYPE_GROUP;
        } else {
            if (findCellItemByPos(position) instanceof List) {
                return TYPE_CHANNEL;
            } else if (findCellItemByPos(position) instanceof Node) {
                return TYPE_PROGRAM;
            } else if (findCellItemByPos(position) instanceof SearchTable) {
                return TYPE_HISTORY;
            } else if (findCellItemByPos(position) instanceof DataModels.NPXEpgTopSearchProgramModel) {
                return TYPE_TOP_SEARCH;
            }
        }
        return -1;
    }

    public class GroupHistoryItemHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView remove;

        public GroupHistoryItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            remove = (ImageView) itemView.findViewById(R.id.remove);
        }
    }


    public class GroupItemHolder extends RecyclerView.ViewHolder {
        TextView title;

        public GroupItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public class ChannelItemHolder extends RecyclerView.ViewHolder {
        LinearLayout container;

        public ChannelItemHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }

    public class ProgramItemHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        ImageView image;

        public ProgramItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public class HistoryItemHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView remove;

        public HistoryItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            remove = (ImageView) itemView.findViewById(R.id.remove);
        }
    }

    private class TopGroupItemHolder extends RecyclerView.ViewHolder {
        TextView title;

        public TopGroupItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}

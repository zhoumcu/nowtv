package com.pccw.nowplayer.activity.search;

import android.support.v7.widget.RecyclerView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ToolBarBaseActivity;
import com.pccw.nowplayer.adapter.SearchListAdapter;
import com.pccw.nowplayer.model.CatalogClient;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.Group;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Validations;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Swifty on 5/9/2016.
 */
public abstract class SearchBaseActivity extends ToolBarBaseActivity {
    protected String searchVariable;

    protected RecyclerView list;
    protected SearchListAdapter searchListAdapter;
    public String searchValue;

    protected void showSearchList(Node result, final boolean detailPage) {
        if (result == null) return;
        ArrayList<Node> channels = result.getSubNodes(NodeType.Channel);
        final ArrayList<Node> programs = result.getSubNodes(NodeType.Program);
        final List<Node> channelDetails = EPGClient.getInstance().getDetailedChannelList(channels);
        if (detailPage) {
            CatalogClient.getInstance().loadDetails(programs, false).always(new AlwaysCallback<List<Node>, Throwable>() {
                @Override
                public void onAlways(Promise.State state, List<Node> resolved, Throwable rejected) {
                    showList(channelDetails, resolved, detailPage);
                }
            });
        }
        showList(channelDetails, programs, detailPage);
    }

    private void showList(List<Node> channels, List<Node> programs, boolean detailPage) {
        Map<Group, List<Object>> map = new TreeMap<>();
        if (!Validations.isEmptyOrNull(channels)) {
            Group group;
            if (detailPage) {
                group = new Group(getString(R.string.channel) + "(" + channels.size() + ")");
            } else {
                group = new Group(getString(R.string.channel));
            }

            ArrayList<Object> tmp = new ArrayList<>();
            tmp.add(channels);
            map.put(group, tmp);
        }

        if (!Validations.isEmptyOrNull(programs)) {
            Group group;
            if (detailPage) {
                group = new Group(getString(R.string.program) + "(" + programs.size() + ")");
            } else {
                group = new Group(getString(R.string.program));
            }
            map.put(group, new ArrayList<Object>(programs));
        }
        searchListAdapter = new SearchListAdapter(map, this, detailPage);
        list.setAdapter(searchListAdapter);
    }
}

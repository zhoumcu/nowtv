package com.pccw.nowplayer.activity.node;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ToolBarBaseActivity;
import com.pccw.nowplayer.adapter.NPXNodeAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.VODClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.TypeUtils;

import org.osito.androidpromise.deferred.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/20/2016.
 */
@Deprecated
public class NodeGridActivity extends ToolBarBaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.recycle_view)
    RecyclerView grid;

    private List<Node> sub_nodes;
    private Node sub_node;
    private Node node;

    @Override
    protected void initIntentData() {
        super.initIntentData();
        sub_nodes = (ArrayList<Node>) getIntent().getSerializableExtra(Constants.ARG_LEAF_NODE_ARRAY);
        sub_node = (Node) getIntent().getSerializableExtra(Constants.ARG_SUB_NODE_SINGLE);
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_node_grid);
        ButterKnife.bind(this);
        title.setText(node == null ? "" : node.getTitle());
        grid.setLayoutManager(RecycleViewManagerFactory.verticalAutoGrid(this));
    }

    @Override
    protected void bindEvents() {
        if (sub_nodes != null) {
            grid.setAdapter(new NPXNodeAdapter(this, sub_nodes));
        } else if (sub_node != null) {
            if (sub_node.isType(NodeType.Premium)) {
                sub_nodes = sub_node.getSubNodes();
                grid.setAdapter(new NPXNodeAdapter(NodeGridActivity.this, sub_nodes));
            } else {
                retrieveProgramList();
            }
        }
    }

    private void retrieveProgramList() {
        VODClient.getInstance().loadSubNodes(node).thenOnMainThread(new Task<List<Node>>() {
            @Override
            public void run(List<Node> list) {
                sub_nodes = list;
                grid.setAdapter(new NPXNodeAdapter(NodeGridActivity.this, sub_nodes));
            }
        });
    }
}

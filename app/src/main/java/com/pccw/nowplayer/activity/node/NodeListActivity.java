package com.pccw.nowplayer.activity.node;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ToolBarBaseActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.OnDemandViewHelper;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.VODClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.Validations;

import org.osito.androidpromise.deferred.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/19/2016.
 */
public class NodeListActivity extends ToolBarBaseActivity {
    @Bind(R.id.btn_play)
    ImageView btnPlay;
    @Bind(R.id.btn_subscribe)
    LinearLayout btnSubscribe;
    @Bind(R.id.channel_head)
    RelativeLayout channelHead;
    @Bind(R.id.container)
    LinearLayout container;
    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.root_lay)
    LinearLayout rootLay;
    @Bind(R.id.title)
    TextView title;
    private Node node;

    private void addSubSections(List<Node> sections) {
        if (sections == null) return;
        if (sections.size() == 1) {
            showGrid(sections.get(0).getSubNodes());
        } else {
            showHorizontalList(sections);
        }
    }

    @Override
    protected void bindEvents() {
        refreshViews();
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_subnode_list);
        ButterKnife.bind(this);
        title.setText(node == null ? "" : node.getTitle());
    }

    private void refreshViews() {

        if (node == null) node = Node.emptyNode();

        if (node.isCat3()) {
            ImageUtils.loadImage(icon, node);
            channelHead.setVisibility(View.VISIBLE);
            btnSubscribe.setVisibility(node.isSubscribed() ? View.GONE : View.VISIBLE);
            btnSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NowPlayerLinkClient.getInstance().executeUrlAction(NodeListActivity.this, Constants.ACTION_LIVE_CHAT);
                }
            });
        }

        VODClient.getInstance().loadSubNodes(node).thenOnMainThread(new Task<List<Node>>() {
            @Override
            public void run(List<Node> data) {

                node.setSubNodes(data);

                ArrayList<Node> products = node.getSubNodes(NodeType.Product);
                ArrayList<Node> branches = node.getSubNodes(NodeType.Branch);

                if (!Validations.isEmptyOrNull(products)) {
                    showGrid(products);
                }
                addSubSections(branches);
            }
        });
    }

    private void showGrid(List<Node> nodes) {
        container.addView(OnDemandViewHelper.generateGrid(NodeListActivity.this, container, nodes));
    }

    private void showHorizontalList(List<Node> branches) {
        for (Node branch : branches) {
            View view;
            if (node.isCat3Parent()) {
                view = OnDemandViewHelper.generateChannelGrid(this, container, branch);
            } else {
                view = OnDemandViewHelper.generateHorizantalList(this, container, branch);
            }
            if (view != null) container.addView(view);
        }
    }
}

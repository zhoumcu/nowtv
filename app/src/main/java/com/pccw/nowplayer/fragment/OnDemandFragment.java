package com.pccw.nowplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.helper.OnDemandViewHelper;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.VODClient;
import com.pccw.nowplayer.model.node.Node;

import org.osito.androidpromise.deferred.Task;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/16/2016.
 */
public class OnDemandFragment extends MainBaseFragment {

    @Bind(R.id.container)
    LinearLayout container;
    Node node;

    @Override
    public View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.fragment_ondemand, parentContainer, false);
        ButterKnife.bind(this, root);
        retrieveData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void retrieveData() {
        VODClient.getInstance().loadLanding().thenOnMainThread(new Task<Node>() {
            @Override
            public void run(Node data) {
                if (isAdded()) {
                    node = data;
                    updateViews();
                }
            }
        });
    }

    @Override
    public ActionBar showActionBar() {
        return new ActionBar(false, getString(R.string.on_demand), false);
    }

    private void updateViews() {
        if (node == null || !isAdded()) return;
        container.removeAllViews();

        List<Node> subNodes = node.getSubNodes(NodeType.Branch);
        if (isAdded()) for (Node subNode : subNodes) {

            View view;
            if (subNode.isType(NodeType.Branch)) {
                view = OnDemandViewHelper.generateHorizantalList(this.getContext(), container, subNode);
            } else {
                view = null;
            }
            if (view != null) container.addView(view);

            //View view = OnDemandViewHelper.generateOndemandHorizantalList(getContext(), container, subNode);
            //if (view != null) {
            //    container.addView(view);
            //}
        }
    }
}

package com.pccw.nowplayer.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.NPXNodeAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.VODClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowplayer.utils.ViewUtils;

import org.osito.androidpromise.deferred.Task;

import java.util.List;

/**
 * Created by Swifty on 5/16/2016.
 */
public class OnDemandViewHelper extends ViewHelper {

    public static View generateOndemandHorizantalList(final Context context, LinearLayout container, final Node subnode) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_part_module, container, false);
        ((TextView) view.findViewById(R.id.title)).setText(subnode.getTitle());
        view.findViewById(R.id.see_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle = generateNodeBundle(subnode, bundle);
                exceAction(context, bundle);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_list);
        recyclerView.setLayoutManager(RecycleViewManagerFactory.horizontalList(context));
        List<Node> nodeList = subnode.getSubNodes(NodeType.Product | NodeType.Cat3, true);
        if (!Validations.isEmptyOrNull(nodeList)) {
            recyclerView.setAdapter(new NPXNodeAdapter(context, nodeList, Constants.MAXHORIZONTALCOUNT));
            return view;
        }
        return null;

    }


    public static View generateChannelGrid(Context context, LinearLayout container, Node subnode) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_part_module, container, false);
        ((TextView) view.findViewById(R.id.title)).setText(subnode.getTitle());
        view.findViewById(R.id.see_more).setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_list);
        recyclerView.setLayoutManager(RecycleViewManagerFactory.verticalGrid(context, ViewUtils.getColumns((Activity) context)));
        recyclerView.setNestedScrollingEnabled(false);
        List<Node> nodeList = subnode.getSubNodes(NodeType.Cat3, true);
        if (!Validations.isEmptyOrNull(nodeList)) {
            recyclerView.setAdapter(new NPXNodeAdapter(context, nodeList));
            return view;
        }
        return null;
    }

    public static View generateHorizantalList(final Context context, LinearLayout container, final Node node) {
        final View view = LayoutInflater.from(context).inflate(R.layout.view_part_module, container, false);
        ((TextView) view.findViewById(R.id.title)).setText(node.getTitle());
        view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        view.findViewById(R.id.see_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (node != null) {
                    Bundle bundle = new Bundle();
                    Node copy = node.copy();
                    copy.setHasMore(true);
                    bundle = generateNodeBundle(copy, bundle);
                    exceAction(context, bundle);
                }
            }
        });
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_list);
        recyclerView.setLayoutManager(RecycleViewManagerFactory.horizontalList(context));
        recyclerView.setNestedScrollingEnabled(false);
        VODClient.getInstance().loadSubNodes(node).thenOnMainThread(new Task<List<Node>>() {
            @Override
            public void run(List<Node> data) {
                node.setSubNodes(data);

                if (data == null || data.isEmpty()) {
                    view.setVisibility(View.GONE);
                } else {
                    if (data.size() > Constants.MAXHORIZONTALCOUNT)
                        view.findViewById(R.id.see_more).setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new NPXNodeAdapter(context, data, Constants.MAXHORIZONTALCOUNT));
                }
                view.findViewById(R.id.progress).setVisibility(View.GONE);
            }
        }).onErrorOnMainThread(new Task<Throwable>() {
            @Override
            public void run(Throwable data) {
                view.findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });

        return view;
    }

    public static View generateLeafGrid(Context context, LinearLayout container, List<Node> sub_nodes) {
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.view_recycler_view, container, false);
        recyclerView.setLayoutManager(RecycleViewManagerFactory.verticalGrid(context, ViewUtils.getColumns((Activity) context)));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new NPXNodeAdapter(context, sub_nodes));
        return recyclerView;
    }

    public static View generateGrid(Context context, LinearLayout rootLay, List<Node> nodes) {
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.view_recycler_view, rootLay, false);
        recyclerView.setLayoutManager(RecycleViewManagerFactory.verticalGrid(context, ViewUtils.getColumns((Activity) context)));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new NPXNodeAdapter(context, nodes));
        return recyclerView;
    }
}

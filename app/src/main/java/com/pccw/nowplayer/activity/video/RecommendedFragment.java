package com.pccw.nowplayer.activity.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.NPXNodeAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.VODFragment;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.CatalogClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowplayer.utils.UIUtils;

import org.jdeferred.DoneCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kevin on 2016/3/26.
 */
public class RecommendedFragment extends VODFragment {

    RecyclerView grid;
    Node node;
    List<Node> recommendedPrograms;


    public static RecommendedFragment getRecommendedFragment(Node node) {
        RecommendedFragment fragment = new RecommendedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void fillView() {
        Activity activity = getActivity();
        if (activity != null && grid != null) {
            grid.setNestedScrollingEnabled(false);
            grid.setLayoutManager(RecycleViewManagerFactory.verticalAutoGrid(activity, TypeUtils.dpToPx(activity, 110)));
            grid.setAdapter(new NPXNodeAdapter(activity, recommendedPrograms, 9));
        }
    }

    public Node getNode() {
        if (node == null) node = UIUtils.getSerializable(this, Constants.ARG_NODE, Node.class);
        return node;
    }

    public void loadData() {
        CatalogClient.getInstance().loadRecommendation(getNode()).then(new DoneCallback<List<Node>>() {
            @Override
            public void onDone(List<Node> result) {
                recommendedPrograms = result;
                fillView();
            }
        });
    }


    public List<Node> getRecommendedPrograms() {
        return recommendedPrograms;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended, null);
        grid = UIUtils.findView(view, R.id.grid, RecyclerView.class);
        loadData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        grid = null;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

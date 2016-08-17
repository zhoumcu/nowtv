package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.NPXNodeAdapter;
import com.pccw.nowplayer.fragment.BaseFragment;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.CatalogClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowplayer.utils.ViewUtils;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/25/2016.
 */
public class MyRecommendedFragment extends BaseFragment {
    @Bind(R.id.progress_lay)
    View progressLay;
    @Bind(R.id.round_bar)
    SegmentTabLayout roundBar;
    @Bind(R.id.recycle_view)
    RecyclerView recycleView;
    @Bind(R.id.empty_text)
    TextView emptyText;
    private View root;
    String[] tabStr;
    RecyclerView.Adapter adapter;
    private List<Node> epgNodes;
    private List<Node> vodNodes;
    List<Node> nodes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_tab_list, container, false);
        ButterKnife.bind(this, root);
        initViews();
        bindViews();
        return root;
    }

    private void initViews() {
        tabStr = new String[]{getString(R.string.coming_up), getString(R.string.on_demand)};
        root.findViewById(R.id.round_bar_lay).setVisibility(View.VISIBLE);
        roundBar.setTabData(tabStr);
        roundBar.setTabPadding(TypeUtils.dpToPx(getContext(), 2));
        recycleView.setLayoutManager(RecycleViewManagerFactory.verticalGrid(getContext(), ViewUtils.getColumns(getActivity())));
        emptyText.setText(getString(R.string.no_recommended_item));
        adapter = new NPXNodeAdapter(getContext(), nodes, true);
        recycleView.setAdapter(adapter);
    }

    private void bindViews() {
        progressLay.setVisibility(View.VISIBLE);
        CatalogClient.getInstance().laodMyNowRecommendations().always(new AlwaysCallback<List<Node>, Throwable>() {
            @Override
            public void onAlways(Promise.State state, List<Node> resolved, Throwable rejected) {
                if (!isAdded()) return;
                progressLay.setVisibility(View.GONE);
                getNodes(resolved);
                refreshViews(roundBar.getCurrentTab());
            }
        });
        roundBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                refreshViews(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void refreshViews(int position) {
        switch (position) {
            case 0:
                if (Validations.isEmptyOrNull(epgNodes)) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }
                nodes.clear();
                nodes.addAll(epgNodes);
                break;
            case 1:
                if (Validations.isEmptyOrNull(vodNodes)) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }
                nodes.clear();
                if (vodNodes != null) nodes.addAll(vodNodes);
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void getNodes(List<Node> resolved) {
        if (resolved == null) return;
        if (epgNodes == null) epgNodes = new ArrayList<>();
        else epgNodes.clear();
        if (vodNodes == null) vodNodes = new ArrayList<>();
        else vodNodes.clear();
        for (Node node : resolved) {
            if (node.isEPG()) {
                epgNodes.add(node);
            } else if (node.isVOD()) {
                vodNodes.add(node);
            }
        }
    }
}

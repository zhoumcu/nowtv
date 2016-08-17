package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.MyNowAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.constant.VideoTypeIndex;
import com.pccw.nowplayer.fragment.BaseFragment;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.WatchListClient;
import com.pccw.nowplayer.model.node.NPXMyNowAddWatchlistItemDataModelNode;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowplayer.utils.ViewUtils;
import com.pccw.nowplayer.widget.DownloadView;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/25/2016.
 */
public class MyWatchListFragment extends BaseFragment {
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
    private List<Node> subNodes = new ArrayList<>();

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
        tabStr = new String[]{getString(R.string.all), getString(R.string.watch_now), getString(R.string.coming_up), getString(R.string.ending_soon)};
        root.findViewById(R.id.round_bar_lay).setVisibility(View.VISIBLE);
        roundBar.setTabData(tabStr);
        roundBar.setTabPadding(TypeUtils.dpToPx(getContext(), 2));
        recycleView.setLayoutManager(RecycleViewManagerFactory.verticalList(getContext()));
        recycleView.addItemDecoration(RecycleViewManagerFactory.getNormalDecoration(getContext()));
        ViewUtils.initWatchListEmptyText(getContext(), emptyText);
        adapter = new MyNowAdapter(getContext(), subNodes);
        recycleView.setAdapter(adapter);
    }

    private void bindViews() {
        progressLay.setVisibility(View.VISIBLE);
        WatchListClient.getInstance().loadWatchListAndDetails().always(new AlwaysCallback<List<Node>, Throwable>() {
            @Override
            public void onAlways(Promise.State state, List<Node> resolved, Throwable rejected) {
                if (!isAdded()) return;
                progressLay.setVisibility(View.GONE);
                subNodes.clear();
                subNodes.addAll(resolved);
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
        if (Validations.isEmptyOrNull(subNodes)) {
            emptyText.setVisibility(View.VISIBLE);
            return;
        } else {
            emptyText.setVisibility(View.GONE);
        }
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

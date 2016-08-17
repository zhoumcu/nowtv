package com.pccw.nowplayer.activity.tvremote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.NPXNodeAdapter;
import com.pccw.nowplayer.fragment.BaseFragment;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.FavoriteChannelsClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.TypeUtils;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by swifty on 27/5/2016.
 */
public class FavoriteFragment extends BaseFragment {

    @Bind(R.id.recycle_view)
    RecyclerView recycleView;
    private View root;
    private List<Node> channels;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_fragment, container, false);
        ButterKnife.bind(this, root);
        FavoriteChannelsClient.getInstance().loadFavoriteChannelList().always(new AlwaysCallback<List<Node>, Throwable>() {
            @Override
            public void onAlways(Promise.State state, List<Node> resolved, Throwable rejected) {
                channels = resolved;
                bindViews();
            }
        });
        return root;
    }

    private void bindViews() {
        if (!isAdded()) return;
        recycleView.setLayoutManager(RecycleViewManagerFactory.verticalAutoGrid(getActivity()));
        recycleView.setAdapter(new NPXNodeAdapter(getActivity(), channels));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

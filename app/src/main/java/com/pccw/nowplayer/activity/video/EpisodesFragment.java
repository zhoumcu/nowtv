package com.pccw.nowplayer.activity.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.EpisodesAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.VODFragment;
import com.pccw.nowplayer.model.node.Node;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/3/26.
 */
@Deprecated
public class EpisodesFragment extends VODFragment implements View.OnClickListener {


    @Bind(R.id.tv_synopsis)
    TextView tvSynopsis;
    @Bind(R.id.tv_more)
    TextView tvMore;
    @Bind(R.id.grid)
    RecyclerView grid;

    public static EpisodesFragment getEpisodeFragment(Node node) {
        EpisodesFragment episodesFragment = new EpisodesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        episodesFragment.setArguments(bundle);
        return episodesFragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_episodes, null);
        ButterKnife.bind(this, view);
        grid.post(new Runnable() {
                      @Override
                      public void run() {
                          Node node = (Node) getArguments().getSerializable(Constants.ARG_NODE);
                          List<Node> episodes = node == null ? new ArrayList<Node>() : node.getEpisodes();

                          grid.setLayoutManager(new LinearLayoutManager(getActivity()));
                          grid.setAdapter(new EpisodesAdapter(getContext(), episodes));

                          tvSynopsis.setText(node.getSynopsis());
                          if (tvSynopsis.getLineCount() < 3) {
                              tvMore.setVisibility(View.GONE);
                          } else {
                              tvMore.setOnClickListener(EpisodesFragment.this);
                          }
                      }
                  }

        );


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        if (tvSynopsis.getLineCount() <= 3) {
            tvSynopsis.setMaxLines(Integer.MAX_VALUE);
        }
    }
}

package com.pccw.nowplayer.activity.video;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.EpisodesAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.VODFragment;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowplayer.utils.TextUtil;
import com.pccw.nowplayer.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/3/26.
 */
public class AboutFragment extends VODFragment implements View.OnClickListener {


    @Bind(R.id.iv_sec_type)
    ImageView ivSecType;
    @Bind(R.id.ll_actors)
    LinearLayout llActors;
    @Bind(R.id.ll_available_on)
    LinearLayout llAvailableOn;
    @Bind(R.id.ll_classification)
    LinearLayout llClassification;
    @Bind(R.id.ll_director)
    LinearLayout llDirector;
    @Bind(R.id.ll_duration)
    LinearLayout llDuration;
    @Bind(R.id.ll_ends)
    LinearLayout llEnds;
    @Bind(R.id.ll_languages)
    LinearLayout llLanguages;
    @Bind(R.id.ll_subtitle)
    LinearLayout llSubtitle;
    @Bind(R.id.ll_trailer)
    LinearLayout llTrailer;
    Node node;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.tv_available_on)
    TextView tvAvailableOn;
    @Bind(R.id.tv_classification)
    TextView tvClassification;
    @Bind(R.id.tv_duration)
    TextView tvDuration;
    @Bind(R.id.tv_ends_value)
    TextView tvEndsValue;
    @Bind(R.id.tv_languages)
    TextView tvLanguages;
    @Bind(R.id.tv_subtitle)
    TextView tvSubtitle;
    @Bind(R.id.tv_synopsis)
    TextView tvSynopsis;
    @Bind(R.id.grid)
    RecyclerView grid;
    @Bind(R.id.ll_info)
    LinearLayout llInfo;

    //    Node program
    public static AboutFragment getAboutFragment(Node node) {
        AboutFragment aboutFragment = new AboutFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        aboutFragment.setArguments(bundle);
        return aboutFragment;
    }

    public void createTextValue(LinearLayout linearLayout, String text) {
        final String title = text == null ? "" : text.trim().replaceAll("\\s+", " ");
        TextView textView = new TextView(getContext());
        textView.setTextSize(11);
        textView.setSingleLine();
        textView.setBackgroundResource(R.drawable.bg_vod_value_selector);
        textView.setTextColor(Color.WHITE);
        textView.setText(title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(getActivity(), Constants.ACTION_SEARCH + ":" + title);
            }
        });
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lllp.setMargins(0, 0, 10, 0);
        linearLayout.addView(textView, lllp);
    }

    private void fillValue(Node node) {
        if (node == null) node = Node.emptyNode();
        long duration = node.getDuration();
        String mins = getString(R.string.mins);
        String minsValues = String.format("%1$d", duration);
        String durationStr = String.format(mins, minsValues);

        UIUtils.setText(tvSubtitle, node.getSubtitleLanguagesText(), llSubtitle);
        UIUtils.setText(tvSynopsis, node.getSynopsis(), true);
        TextUtil.setMore(tvSynopsis,"... ",getString(R.string.more));
        tvSynopsis.setOnClickListener(this);
        UIUtils.setImage(ivSecType, node.getClassificationImageResourceID(), true);
        UIUtils.setText(tvEndsValue, node.getEndDateText(), llEnds);
        UIUtils.setText(tvLanguages, node.getLanguagesText(), llLanguages);
        UIUtils.setText(tvDuration, durationStr, llDuration);
        UIUtils.setText(tvClassification, node.getClassificationText(), llClassification);
        UIUtils.setText(tvAvailableOn, node.getAvailablePlatformsText(), llAvailableOn);

        if (llTrailer != null) {
            Node trialer = node.getTrailer();
            if (trialer != null) {
                llTrailer.setOnClickListener(this);
                llTrailer.setVisibility(View.VISIBLE);
            } else {
                llTrailer.setVisibility(View.GONE);
            }
        }

        String delimiter = ",";
        String delimiter2 = "、";
        if (llDirector != null) {
            String director = node.getDirectorsText();
            if (Check.isEmpty(director)) {
                llDirector.setVisibility(View.GONE);
            } else {
                String[] directors = director.split(delimiter);
                String[] directors2 = director.split(delimiter2);
                if(directors2.length>directors.length){
                    directors = directors2;
                }
                for (int i = 0; i < directors.length; i++) {
                    createTextValue(llDirector, directors[i]);
                }
            }
        }

        String actor = node.getActorsText();
        if (llActors != null) {
            if (Check.isEmpty(actor)) {
                llActors.setVisibility(View.GONE);
            } else {
                String[] actors = actor.split(delimiter);
                String[] actors2 = actor.split(delimiter2);
                if(actors2.length>actors.length){
                    actors = actors2;
                }
                for (int i = 0; i < actors.length; i++) {
                    createTextValue(llActors, actors[i]);
                }
            }
        }

        final List<Node> episodes = node == null ? new ArrayList<Node>() : node.getEpisodes();
        if (!Check.isEmpty(episodes)) {
            grid.setLayoutManager(new LinearLayoutManager(getActivity()));
            grid.setAdapter(new EpisodesAdapter(getContext(), episodes));
        }

        if (!Check.isEmpty(episodes)) {
            llInfo.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_trailer) {
            if (node != null) {
                Node trailerNode = node.getTrailer();
                if (trailerNode != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.ARG_NODE, trailerNode);
                    NowPlayerLinkClient.getInstance().executeUrlAction(getActivity(), Constants.ACTION_VIDEO_PLAYER, bundle);
                }
            }
        } else if (v.getId() == R.id.tv_synopsis) {
            tvSynopsis.setText(node.getSynopsis());
            llInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vod_about, null);
        ButterKnife.bind(this, view);
        node = (Node) getArguments().getSerializable(Constants.ARG_NODE);
        fillValue(node);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

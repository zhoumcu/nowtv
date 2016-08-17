package com.pccw.nowplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.daimajia.slider.library.SliderLayout;
import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.LandingViewHelper;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.CatalogClient;
import com.pccw.nowplayer.model.FavoriteChannelsClient;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowplayer.utils.ViewUtils;

import org.jdeferred.DoneCallback;
import org.osito.androidpromise.deferred.Task;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/5/2016.
 */
public class LandingFragment extends MainBaseFragment {

    private final boolean autoScroll = false;
    List<Node> categories;
    @Bind(R.id.container)
    LinearLayout container;
    private List<SliderLayout> sliderLayouts;

    private void addFavoriteList(final LinearLayout container, final Node cat) {
        final FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        FavoriteChannelsClient.getInstance().loadFavoriteChannelList().done(new DoneCallback<List<Node>>() {
            @Override
            public void onDone(List<Node> result) {
                cat.setSubNodes(result);
                View view = LandingViewHelper.generateHorizontalChannelList(getContext(), frameLayout, cat, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_TV_GUIDE);
                    }
                });
                if (view != null) frameLayout.addView(view);
            }
        });
        container.addView(frameLayout);
    }

    @Override
    public View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.fragment_landing, parentContainer, false);
        ButterKnife.bind(this, root);
        initData();
        PlayerApplication.needRefreshFragment = false;
        return root;
    }

    private void initData() {
        CatalogClient.getInstance().loadLandingCatalog().thenOnMainThread(new Task<List<Node>>() {
            @Override
            public void run(List<Node> data) {
                categories = data;
                if (isAdded()) refreshViews();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (autoScroll && sliderLayouts != null) {
            for (SliderLayout sliderLayout : sliderLayouts) {
                sliderLayout.startAutoCycle();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (autoScroll && sliderLayouts != null) {
            for (SliderLayout sliderLayout : sliderLayouts) {
                sliderLayout.stopAutoCycle();
            }
        }
    }

    private void refreshViews() {
        container.removeAllViews();
        if (categories == null) return;

        for (final Node cat : categories) {
            if (cat.isType(NodeType.LandingBanner)) {
                View view = LandingViewHelper.generateSlideViews(getContext(), container, cat);
                if (view != null) {
                    ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.middle_padding));
                    container.addView(view);
                }
            } else if (cat.isType(NodeType.LandingResume)) {
                List<View> views = LandingViewHelper.generateResumeViews(getContext(), container, cat);
                if (!Validations.isEmptyOrNull(views)) {
                    for (View view : views)
                        container.addView(view);
                }
            } else if (cat.isType(NodeType.LandingLive)) {
                addFavoriteList(container, cat);
            } else if (cat.isType(NodeType.LandingRecommendation)) {
                View view = LandingViewHelper.generateHorizontalList(getContext(), container, cat, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_MY_NOW + "/" + 1);
                    }
                });
                if (view != null) container.addView(view);
            } else if (cat.isType(NodeType.LandingWatchList)) {
                View view = LandingViewHelper.generateHorizontalList(getContext(), container, cat, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_MY_NOW);
                    }
                });
                if (view != null) container.addView(view);
            } else if (cat.isType(NodeType.AdSection)) {
                View view = LandingViewHelper.generateAdView(getContext(), container, cat);
                if (view != null) container.addView(view);
            } else {
                View view = LandingViewHelper.generateHorizontalList(getContext(), container, cat);
                if (view != null) container.addView(view);
            }
        }
        sliderLayouts = ViewUtils.findViewsFromParent(getView(), SliderLayout.class);
    }

    @Override
    public ActionBar showActionBar() {
        return new ActionBar(true, null, true);
    }
}

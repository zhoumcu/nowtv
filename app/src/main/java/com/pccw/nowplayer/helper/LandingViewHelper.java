package com.pccw.nowplayer.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.NPXNodeAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowplayer.utils.ViewUtils;
import com.pccw.nowtv.nmaf.adEngine.NMAFAdEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swifty on 5/3/2016.
 */
public class LandingViewHelper extends ViewHelper {
    public static View generateHorizontalList(final Context context, ViewGroup container, final Node dataCat) {
        return generateHorizontalList(context, container, dataCat, null);
    }

    public static View generateHorizontalList(final Context context, ViewGroup container, final Node dataCat, final View.OnClickListener seeAll) {

        if (dataCat == null) return null;
        ArrayList<Node> products = dataCat.getSubNodes();
        if (products == null || Validations.isEmptyOrNull(products)) return null;

        View view = LayoutInflater.from(context).inflate(R.layout.view_part_module, container, false);
        ((TextView) view.findViewById(R.id.title)).setText(dataCat.getTitle());
        if (seeAll == null) {
            view.findViewById(R.id.see_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataCat != null) {
                        Bundle bundle = new Bundle();
                        Node copy = dataCat.copy();
                        copy.setHasMore(false);
                        bundle = generateNodeBundle(copy, bundle);
                        exceAction(context, bundle);
                    }
                }
            });
        } else {
            view.findViewById(R.id.see_more).setOnClickListener(seeAll);
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_list);
        recyclerView.setLayoutManager(RecycleViewManagerFactory.horizontalList(context));
        recyclerView.setAdapter(new NPXNodeAdapter(context, products, Constants.MAXHORIZONTALCOUNT, true));
        return view;
    }

    public static List<View> generateResumeViews(final Context context, ViewGroup container, final Node dataCat) {

        if (dataCat == null) return null;
        ArrayList<Node> products = dataCat.getPrograms();
        if (products == null || Validations.isEmptyOrNull(products)) return null;

        List<View> views = new ArrayList<>();
        for (Node data : products) {
            final View view = LayoutInflater.from(context).inflate(R.layout.view_resume_cell, container, false);
            ImageUtils.loadImage((ImageView) view.findViewById(R.id.image), data);
            ((TextView) view.findViewById(R.id.title)).setText(context.getString(R.string.resume_watching));
            ((TextView) view.findViewById(R.id.subtitle)).setText(data.getTitle());
            ((TextView) view.findViewById(R.id.time)).setText(data.getRemarks());
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtils.dismissViewY(view);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.ARG_NODE, dataCat);
                    NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_PLAYER, bundle);
                }
            });
            views.add(view);
        }
        return views;
    }

    public static View generateSlideViews(final Context context, ViewGroup parent, final Node dataCat) {

        if (dataCat == null) return null;
        ArrayList<Node> banners = dataCat.getPrograms();
        if (banners == null || Validations.isEmptyOrNull(banners)) return null;

        View view = LayoutInflater.from(context).inflate(R.layout.slider, parent, false);
        SliderLayout sliderLayout = (SliderLayout) view.findViewById(R.id.slider);
        for (final Node data : banners) {
            DefaultSliderView sliderView = new DefaultSliderView(context);
            // initialize a SliderLayout
            sliderView
                    .image(data.getImageUrl())
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            exceNodeAction(context, data);
                        }
                    });
            sliderLayout.addSlider(sliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
        sliderLayout.setCustomIndicator((PagerIndicator) view.findViewById(R.id.custom_indicator));
        return view;
    }

    public static View generateAdView(Context context, LinearLayout container, Node cat) {
        if (cat == null || Validations.isEmptyOrNull(cat.getSubNodes()) || !(context instanceof Activity))
            return null;
        for (Node node : cat.getSubNodes()) {
            if (node.isType(NodeType.AdBanner)) {
                if (!TextUtils.isEmpty(node.getNodeId())) {
                    FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.view_ad_container, container, false);
                    NMAFAdEngine.getSharedInstance().loadBannerAd((Activity) context, node.getNodeId(), null, frameLayout, null);
                    return frameLayout;
                }
            }
        }
        return null;
    }

    public static View generateHorizontalChannelList(final Context context, FrameLayout container, final Node dataCat, View.OnClickListener seeAll) {
        if (dataCat == null) return null;
        ArrayList<Node> products = dataCat.getSubNodes();
        if (products == null || Validations.isEmptyOrNull(products)) return null;

        View view = LayoutInflater.from(context).inflate(R.layout.view_part_module, container, false);
        ((TextView) view.findViewById(R.id.title)).setText(dataCat.getTitle());
        if (seeAll == null) {
            view.findViewById(R.id.see_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataCat != null) {
                        Bundle bundle = new Bundle();
                        Node copy = dataCat.copy();
                        copy.setHasMore(true);
                        bundle = generateNodeBundle(copy, bundle);
                        exceAction(context, bundle);
                    }
                }
            });
        } else {
            view.findViewById(R.id.see_more).setOnClickListener(seeAll);
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_list);
        recyclerView.setLayoutManager(RecycleViewManagerFactory.horizontalList(context));
        recyclerView.setAdapter(new NPXNodeAdapter(context, products, Constants.MAXHORIZONTALCOUNT));
        return view;
    }
}

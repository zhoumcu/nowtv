package com.pccw.nowplayer.activity.mynow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.MainBaseFragment;
import com.pccw.nowplayer.utils.TypeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/24/2016.
 */
public class MyNowFragment extends MainBaseFragment {
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    private int initSubFragmentIndex;

    private void bindViews() {
        viewpager.setAdapter(new DatePager(getChildFragmentManager()));
        tabs.setupWithViewPager(viewpager);
        tabs.setSelectedTabIndicatorHeight((int) getResources().getDimension(R.dimen.indicator_height));
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.orange));
        tabs.setTabTextColors(getResources().getColor(R.color.now_grey), getResources().getColor(R.color.white));
        tabs.getTabAt(initSubFragmentIndex).select();
    }

    @Override
    public View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.fragment_my_now, parentContainer, false);
        initSubFragmentIndex = getArguments().getInt(Constants.ARG_INDEX, 0);
        ButterKnife.bind(this, root);
        bindViews();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public ActionBar showActionBar() {
        return new ActionBar(false, getString(R.string.my_now), false);
    }

    private class DatePager extends FragmentPagerAdapter {

        String[] title = new String[]{getString(R.string.watch_list), getString(R.string.recommended), getString(R.string.saved), getString(R.string.downloads), getString(R.string.tv_recordings), getString(R.string.history)};

        public DatePager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MyWatchListFragment();
                case 1:
                    return new MyRecommendedFragment();
                case 2:
                    return new MySavedFragment();
                case 3:
                    return new MyDownloadeFragment();
                case 4:
                    return new MyTVRecordFragment();
                case 5:
                    return new MyHistoryFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            super.getPageTitle(position);
            return title[position];
        }
    }
}

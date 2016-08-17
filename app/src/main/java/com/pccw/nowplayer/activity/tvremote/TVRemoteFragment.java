package com.pccw.nowplayer.activity.tvremote;

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
import com.pccw.nowplayer.activity.IActionBar;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.MainBaseFragment;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowplayer.utils.TypeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by swifty on 27/5/2016.
 */
public class TVRemoteFragment extends MainBaseFragment implements IActionBar {

    @Bind(R.id.cover_lay)
    View coverLay;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewpager)
    ViewPager viewpager;

    private void bindViews() {
        if (!NowIDClient.getInstance().isFSABound()) {
            NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_FSA_BINDING);
        }
        setFragmentEnabled(DeviceManager.getInstance().hasConnectDevice());
        viewpager.setAdapter(new RemoteFragmentAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(viewpager);
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.orange));
        tabs.setTabTextColors(getResources().getColor(R.color.now_grey), getResources().getColor(R.color.white));
        tabs.setSelectedTabIndicatorHeight(TypeUtils.dpToPx(getContext(), 1));
    }

    @Override
    public View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.fragment_tv_remote, parentContainer, false);
        ButterKnife.bind(this, root);
        bindViews();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setFragmentEnabled(boolean enabled) {
        if (enabled) {
            coverLay.setVisibility(View.GONE);
        } else {
            coverLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ActionBar showActionBar() {
        String subTitle = null;
        if (!DeviceManager.getInstance().hasConnectDevice()) {
            subTitle = getString(R.string.select_device);
        } else {
            subTitle = DeviceManager.getInstance().getConnectDevice().name;
        }
        return new ActionBar(false, getString(R.string.tv_remote), false, subTitle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_STB_BINDING);
            }
        });
    }

    private class RemoteFragmentAdapter extends FragmentPagerAdapter {
        public RemoteFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new RemoteFragment();
            } else if (position == 1) {
                return new NumberPadFragment();
            } else if (position == 2) {
                return new FavoriteFragment();
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.remote);
            } else if (position == 1) {
                return getString(R.string.number_pad);
            } else if (position == 2) {
                return getString(R.string.favourite);
            } else {
                return null;
            }
        }
    }
}

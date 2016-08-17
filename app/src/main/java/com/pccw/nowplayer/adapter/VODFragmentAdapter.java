package com.pccw.nowplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Kevin on 2016/3/26.
 */
public class VODFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> myFragmentList;
    private List<String> titleList;


    public VODFragmentAdapter(FragmentManager fm, List<Fragment>data, List<String>titleList){
        super(fm);
        this.titleList = titleList;
        this.myFragmentList = data;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment page = myFragmentList.get(position);



        return page;
    }

    @Override
    public int getCount() {
        return myFragmentList.size();
    }
}

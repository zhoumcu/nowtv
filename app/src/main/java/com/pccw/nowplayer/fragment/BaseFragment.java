package com.pccw.nowplayer.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Swifty on 5/14/2016.
 */
public class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();

    public boolean isSameWith(MainBaseFragment mainBaseFragment) {
        if (mainBaseFragment == null) return false;
        return this.getClass().getName().equals(mainBaseFragment.getClass().getName());
    }
}

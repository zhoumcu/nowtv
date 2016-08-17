package com.pccw.nowplayer.fragment;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

/**
 * Created by Kevin on 2016/7/21.
 */
public class VODFragment extends Fragment {

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

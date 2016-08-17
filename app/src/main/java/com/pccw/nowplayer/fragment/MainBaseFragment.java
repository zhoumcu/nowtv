package com.pccw.nowplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.activity.IActionBar;
import com.pccw.nowplayer.activity.MainActivity;

/**
 * Created by Swifty on 5/5/2016.
 */
public abstract class MainBaseFragment extends BaseFragment implements IActionBar {


    private LayoutInflater inflater;
    private ViewGroup parentContainer;
    private FrameLayout root;

    public abstract View createViews(LayoutInflater inflater, ViewGroup parentContainer);

    public void finish() {
        if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.parentContainer = container;
        if (getActivity() instanceof MainActivity) {
            ActionBar actionBar = showActionBar();
            ((MainActivity) getActivity()).changeActionBar(actionBar);
        }
        if (root != null) {
            return root;
        }
        root = new FrameLayout(getContext());
        root.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(createViews(inflater, root));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).resetToDefaultActionBar();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PlayerApplication.needRefreshFragment) {
            if (getActivity() instanceof MainActivity) {
                ActionBar actionBar = showActionBar();
                ((MainActivity) getActivity()).changeActionBar(actionBar);
            }
            if (inflater != null && parentContainer != null) {
                root.removeAllViews();
                root.addView(createViews(inflater, parentContainer));
            }
        }
    }
}

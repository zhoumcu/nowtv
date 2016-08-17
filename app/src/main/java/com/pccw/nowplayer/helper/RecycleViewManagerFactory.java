package com.pccw.nowplayer.helper;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.custom.GridAutofitLayoutManager;
import com.pccw.nowplayer.utils.TypeUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Swifty on 3/23/2016.
 */
public class RecycleViewManagerFactory {

    public static LinearLayoutManager horizontalList(Context context) {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        return layoutManager;
    }

    public static LinearLayoutManager verticalList(Context context) {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        return layoutManager;
    }

    public static LinearLayoutManager verticalGrid(Context context, int columns) {
        GridLayoutManager layoutManager
                = new GridLayoutManager(context, columns, GridLayoutManager.VERTICAL, false);
        return layoutManager;
    }

    public static LinearLayoutManager verticalAutoGrid(Context context, int columnWidth) {
        GridLayoutManager layoutManager
                = new GridAutofitLayoutManager(context, columnWidth, GridLayoutManager.VERTICAL, false);
        return layoutManager;
    }

    public static LinearLayoutManager verticalAutoGrid(Context context) {
        GridLayoutManager layoutManager
                = new GridAutofitLayoutManager(context, TypeUtils.dpToPx(context, 110), GridLayoutManager.VERTICAL, false);
        return layoutManager;
    }

    public static RecyclerView.ItemDecoration getNormalDecoration(Context context) {
        RecyclerView.ItemDecoration itemDecoration = new HorizontalDividerItemDecoration.Builder(context)
                .colorResId(R.color.light_grey).margin(10, 0).size(1)
                .build();
        return itemDecoration;
    }
}

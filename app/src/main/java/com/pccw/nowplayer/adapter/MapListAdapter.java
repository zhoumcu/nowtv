package com.pccw.nowplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Swifty on 5/4/16.
 */
public abstract class MapListAdapter<GroupCellType, ItemCellType> extends BaseAdapter<RecyclerView.ViewHolder> {
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_ITEM = 1;

    protected Map<GroupCellType, List<ItemCellType>> listMap;
    protected Context context;

    public MapListAdapter(Map<GroupCellType, List<ItemCellType>> listMap, Context context) {
        if (listMap != null && context != null) {
            this.listMap = listMap;
            this.context = context;
        }
    }

    @Override
    public int getItemCount() {
        return listMap.size() + getAllCount();
    }

    protected int getAllCount() {
        int count = 0;
        for (Map.Entry<GroupCellType, List<ItemCellType>> pair : listMap.entrySet()) {
            count += (pair.getValue()).size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return findTypeByPos(position);
    }

    protected int findTypeByPos(int position) {
        int currentPoint = 0;
        Set<GroupCellType> keySet = listMap.keySet();
        for (GroupCellType aKeySet : keySet) {
            if (position < currentPoint) return TYPE_ITEM;
            if (position == currentPoint) return TYPE_GROUP;
            currentPoint += listMap.get(aKeySet).size() + 1;
        }
        //last group return call item
        return TYPE_ITEM;
    }

    protected GroupCellType findGroupCellByPos(int position) {
        int currentPoint = 0;
        Set<GroupCellType> keySet = listMap.keySet();
        for (GroupCellType aKeySet : keySet) {
            if (position == currentPoint)
                return aKeySet;
            currentPoint += listMap.get(aKeySet).size() + 1;
        }
        return null;
    }

    protected ItemCellType findCellItemByPos(int position) {
        int currentPoint = 0;
        GroupCellType lastKey = null;
        Set<GroupCellType> keySet = listMap.keySet();
        for (GroupCellType aKeySet : keySet) {
            if (position < currentPoint)
                return listMap.get(lastKey).get(listMap.get(lastKey).size() - (currentPoint - position));
            currentPoint += listMap.get(aKeySet).size() + 1;
            lastKey = aKeySet;
        }
        return listMap.get(lastKey).get(listMap.get(lastKey).size() - (currentPoint - position));
    }
}
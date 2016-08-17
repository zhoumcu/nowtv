package com.pccw.nowplayer.utils;

import java.util.List;

/**
 * Created by kriz on 2016-07-19.
 */
public class ListUtils {

    public static <T> T first(List<T> list) {
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }

    public static <T> T last(List<T> list) {
        if (list == null || list.size() == 0) return null;
        return list.get(list.size() - 1);
    }

    public static <T> java.util.List<T> make(T... objects) {
        java.util.List<T> ret = new java.util.ArrayList<>();
        if (objects != null) for (T t : objects) {
            ret.add(t);
        }
        return ret;
    }
}

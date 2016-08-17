package com.pccw.nowplayer.utils;

import android.text.TextUtils;

import java.util.Collection;

/**
 * Created by kriz on 20/7/2016.
 */
public class Is {
    public static boolean different(Object a, Object b) {
        return !same(a, b);
    }

    public static boolean empty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean empty(CharSequence s) {
        return TextUtils.isEmpty(s);
    }

    public static boolean equal(Object a, Object b) {
        return same(a, b);
    }

    public static boolean notEmpty(CharSequence s) {
        return !TextUtils.isEmpty(s);
    }

    public static boolean notEmpty(Collection<?> collection) {
        return collection != null && collection.size() > 0;
    }

    public static boolean same(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}

package com.pccw.nowplayer.utils;

/**
 * Created by kriz on 18/5/2016.
 */
public interface TraversalCallback<T> {
    void visit(TraversalInfo<T> info);
}

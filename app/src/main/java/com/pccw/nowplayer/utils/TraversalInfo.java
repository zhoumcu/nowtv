package com.pccw.nowplayer.utils;

/**
 * Created by kriz on 18/5/2016.
 */
public class TraversalInfo<T> {
    public T data;
    public int level;

    public TraversalInfo() {
    }

    public TraversalInfo(T data, int level) {
        this.data = data;
        this.level = level;
    }
}

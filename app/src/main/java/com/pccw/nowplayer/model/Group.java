package com.pccw.nowplayer.model;

/**
 * Created by Swifty on 5/10/2016.
 */
public class Group implements Comparable<Group> {
    public String title;

    public Group(String title) {
        this.title = title;
    }

    @Override
    public int compareTo(Group another) {
        return title.compareTo(another.title);
    }
}

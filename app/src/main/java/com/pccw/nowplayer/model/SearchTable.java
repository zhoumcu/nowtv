package com.pccw.nowplayer.model;

import com.orm.dsl.Unique;
import com.pccw.nowplayer.model.db_orm.NowModel;

/**
 * Created by Swifty on 3/23/2016.
 */
public class SearchTable extends NowModel {

    @Unique
    public String search_value;
    public long timestamp;
    public String user;

    public SearchTable() {

    }

    public SearchTable(String search_value, long timestamp, String user) {
        this.search_value = search_value;
        this.timestamp = timestamp;
        this.user = user;
    }
}

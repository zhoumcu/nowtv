package com.pccw.nowplayer.model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Swifty on 5/13/2016.
 */
public class TVGuideModel implements Serializable {

    public static class Genre implements Serializable {
        public String key;
        public String id;
        public String name;
        public List<Node> channelList;

        public static Genre create(DataModels.NPXEpgGetChannelGenreGenresModel genre, SparseArray<Node> channelMap) {
            Genre ret = new Genre();
            ret.key = genre.genreId;
            ret.id = genre.genreId;
            ret.name = genre.genreName;

            List<Node> channels = new ArrayList<>();
            if (genre.channels != null) for (DataModels.NPXEpgGetChannelGenreChannelsModel ch : genre.channels) {
                Node channel = channelMap.get(ch.channelId);
                if (channel != null) channels.add(channel);
            }
            ret.channelList = channels;

            return ret;
        }
    }
}

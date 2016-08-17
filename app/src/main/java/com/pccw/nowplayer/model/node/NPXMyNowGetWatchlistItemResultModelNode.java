package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.mynow.DataModels;

/**
 * Created by Swifty on 5/25/2016.
 */
@NodeWrapper(underlyingClass=DataModels.NPXMyNowGetWatchlistItemResultModel.class)
public class NPXMyNowGetWatchlistItemResultModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXMyNowGetWatchlistItemResultModel data = (DataModels.NPXMyNowGetWatchlistItemResultModel)object;

        setChannelId(data.channelId);
        setCid(data.cid);
        setImageUrl(data.potraitImage);
        setTitle(data.title);
        if (isPremiumCatalog()) {
            addTypeMask(NodeType.Premium);
        }
        if ("product".equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.Program);
        } else if ("series".equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.Series);
        }
    }
}

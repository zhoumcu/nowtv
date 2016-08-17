package com.pccw.nowplayer.model.node;

import com.pccw.nowtv.nmaf.npx.mynow.DataModels;

/**
 * Created by Swifty on 5/25/2016.
 */
@NodeWrapper(underlyingClass=DataModels.NPXMyNowGetWatchlistItemOutputModel.class)
public class NPXMyNowGetWatchlistItemOutputModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXMyNowGetWatchlistItemOutputModel data = (DataModels.NPXMyNowGetWatchlistItemOutputModel)object;
        addSubNodes(data.response);
    }
}

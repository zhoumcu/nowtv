package com.pccw.nowplayer.model.node;

import com.pccw.nowtv.nmaf.npx.mynow.DataModels;

import java.util.Date;

/**
 * Created by Kriz on 2016-7-2.
 */
@NodeWrapper(underlyingClass = DataModels.NPXMyNowAddWatchlistItemDataModel.class)
public class NPXMyNowAddWatchlistItemDataModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXMyNowAddWatchlistItemDataModel data = (DataModels.NPXMyNowAddWatchlistItemDataModel) object;

        setNodeId(data.itemId);
        setMyNowItemType(data.itemType);
        setCid(data.cid);
        setSeriesId(data.sid);
        setImageUrl(data.imagePath);
        setAddDate(new Date(data.addDate));
        // setLastViewDate(data.lastViewDate);
    }
}
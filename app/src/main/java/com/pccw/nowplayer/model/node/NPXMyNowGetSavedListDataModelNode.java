package com.pccw.nowplayer.model.node;

import com.pccw.nowtv.nmaf.npx.mynow.DataModels;

/**
 * Created by Kriz on 2016-7-5.
 */
@NodeWrapper(underlyingClass = DataModels.NPXMyNowGetSavedListDataModel.class)
public class NPXMyNowGetSavedListDataModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXMyNowGetSavedListDataModel data = (DataModels.NPXMyNowGetSavedListDataModel) object;

        setMyNowItemType(data.type);
        setChannelId(data.channelId);
        setNodeId(data.id);
        setImageUrl(data.portraitImage);
        // setRecordOrder(data.recordOrder);
        setTitle(data.title);
    }
}
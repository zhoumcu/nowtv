package com.pccw.nowplayer.model.node;

import android.text.TextUtils;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-6-17.
 */
@NodeWrapper(underlyingClass=DataModels.NPXGetRecommendationResultModel.class)
public class NPXGetRecommendationResultModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetRecommendationResultModel data = (DataModels.NPXGetRecommendationResultModel)object;

        setChannelId(data.channelId);
        setNodeId(data.id);
        setImageUrl(data.portraitImage);
        setTitle(data.title);

        if ("epg".equals(data.type)) {
            addTypeMask(NodeType.EPG);
            if (TextUtils.isEmpty(data.id)) {
                addTypeMask(NodeType.Channel);
            } else {
                addTypeMask(NodeType.Program);
            }
        } else if ("vod".equals(data.type)) {
            addTypeMask(NodeType.VOD);
            if (!TextUtils.isEmpty(data.id)) {
                addTypeMask(NodeType.Program);
            }
        } else {
            // Unknown type!
        }
    }
}
package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-6-18.
 */
@NodeWrapper(underlyingClass = DataModels.NPXEpgChannelListChannelModel.class)
public class NPXEpgChannelListChannelModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXEpgChannelListChannelModel data = (DataModels.NPXEpgChannelListChannelModel) object;

        addTypeMask(NodeType.EPG | NodeType.Channel);

        setNodeId(data.id);
        setChannelId(data.id);
        setTitle(data.name);
        setTypeMask(NodeType.OnTV, true);
        setTypeMask(NodeType.OnApp, data.isApp);
        setSubscribed(data.isSubscribed);
        // setAppTickerTemplateName(data.appTickerTemplateName);
        // setAppTickerTemplate(data.appTickerTemplate);
    }
}
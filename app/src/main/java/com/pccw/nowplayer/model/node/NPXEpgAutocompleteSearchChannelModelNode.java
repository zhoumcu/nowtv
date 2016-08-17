package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-7-20.
 */
@NodeWrapper(underlyingClass = DataModels.NPXEpgAutocompleteSearchChannelModel.class)
public class NPXEpgAutocompleteSearchChannelModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXEpgAutocompleteSearchChannelModel data = (DataModels.NPXEpgAutocompleteSearchChannelModel) object;

        addTypeMask(NodeType.EPG | NodeType.Channel);
        setNodeId("" + data.ch_id);
        setChannelId(data.ch_id);
        setTitle(data.ch_name_long_en_us, data.ch_name_long_zh_tw);
        // setCh_name_short_zh_tw(data.ch_name_short_zh_tw);
        // setCh_name_short_en_us(data.ch_name_short_en_us);
    }
}
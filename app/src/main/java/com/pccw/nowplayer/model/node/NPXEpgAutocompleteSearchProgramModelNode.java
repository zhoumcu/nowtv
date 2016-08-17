package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-7-20.
 */
@NodeWrapper(underlyingClass = DataModels.NPXEpgAutocompleteSearchProgramModel.class)
public class NPXEpgAutocompleteSearchProgramModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXEpgAutocompleteSearchProgramModel data = (DataModels.NPXEpgAutocompleteSearchProgramModel) object;


        if ("epg".equals(data.type)) {

            setNodeId(data.p_vimProgId);
            setChannelId(data.p_channelId);
            setTitle(data.p_name_en_us, data.p_name_zh_tw);
            setSeriesName(data.p_series_name_en_us, data.p_series_name_zh_tw);
            addTypeMask(NodeType.EPGProgram);

        } else if ("product".equals(data.type)) {

            setSeriesId(data.product_series_id_t);
            setNodeId(data.product_id_t);
            setTitle(data.product_en_us_name_t, data.product_zh_tw_name_t);
            setSeriesName(data.series_en_us_name_t, data.series_zh_tw_name_t);
            addTypeMask(NodeType.VODProgram);
        }

        // setP_key(data.p_key);
        // setCommon_brand_name(data.common_brand_name);
    }
}
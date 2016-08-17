package com.pccw.nowplayer.model.node;

import android.text.TextUtils;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels;

/**
 * Created by Kriz on 2016-7-4.
 */
@NodeWrapper(underlyingClass = DataModels.NPXTAGetTaVodEpgPreferenceParallelRecommendationRecommendationsModel.class)
public class NPXTAGetTaVodEpgPreferenceParallelRecommendationRecommendationsModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXTAGetTaVodEpgPreferenceParallelRecommendationRecommendationsModel data = (DataModels.NPXTAGetTaVodEpgPreferenceParallelRecommendationRecommendationsModel) object;

        if ("epg".equals(data.type)) {
            addTypeMask(NodeType.EPG);

            setChannelId(data.ch_id);

            if (!TextUtils.isEmpty(data.p_vimProgId)) {
                setNodeId(data.p_vimProgId);
                addTypeMask(NodeType.Program);
            } else {
                setNodeId(data.ch_id);
                addTypeMask(NodeType.Channel);
            }

        } else if ("vod".equals(data.type)) {

            addTypeMask(NodeType.VOD);

            setSeriesId(data.product_series_id_t);

            if (!TextUtils.isEmpty(data.product_id_t)) {
                setNodeId(data.product_id_t);
                addTypeMask(NodeType.Program);
            } else if (!TextUtils.isEmpty(data.product_series_id_t)) {
                setNodeId(data.product_series_id_t);
                addTypeMask(NodeType.Series);
            }

        }

        // setProduct_library_id_t(data.product_library_id_t);
        // setProduct_series_id_t(data.product_series_id_t);
        // setCh_id(data.ch_id);
        // setP_vimProgId(data.p_vimProgId);
        // setType(data.type);
    }
}
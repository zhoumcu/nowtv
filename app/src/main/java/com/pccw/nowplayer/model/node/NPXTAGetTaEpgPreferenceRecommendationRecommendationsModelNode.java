package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels;

/**
 * Created by Swifty on 7/2/2016.
 */
@NodeWrapper(underlyingClass = DataModels.NPXTAGetTaEpgPreferenceRecommendationRecommendationsModel.class)
public class NPXTAGetTaEpgPreferenceRecommendationRecommendationsModelNode extends Node {
    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXTAGetTaEpgPreferenceRecommendationRecommendationsModel data = (DataModels.NPXTAGetTaEpgPreferenceRecommendationRecommendationsModel) object;

        setChannelId(data.ch_id);
        setNodeId(data.p_vimProgId);
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

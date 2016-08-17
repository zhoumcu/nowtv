package com.pccw.nowplayer.model.node;

import android.text.TextUtils;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels;

/**
 * Created by Kriz on 2016-7-8.
 */
@NodeWrapper(underlyingClass = DataModels.NPXTAGetTaVodPreferenceTopListBackupRecommendationRecommendationsModel.class)
public class NPXTAGetTaVodPreferenceTopListBackupRecommendationRecommendationsModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXTAGetTaVodPreferenceTopListBackupRecommendationRecommendationsModel data = (DataModels.NPXTAGetTaVodPreferenceTopListBackupRecommendationRecommendationsModel) object;

        addTypeMask(NodeType.VOD);
        if (!TextUtils.isEmpty(data.product_id_t)) {
            addTypeMask(NodeType.Program);
        }

        setNodeId(data.product_id_t);
        setLibraryId(data.product_library_id_t);
    }
}
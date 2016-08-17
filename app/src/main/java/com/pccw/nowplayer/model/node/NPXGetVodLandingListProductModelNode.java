package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-6-16.
 */
@NodeWrapper(underlyingClass=DataModels.NPXGetVodLandingListProductModel.class)
public class NPXGetVodLandingListProductModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodLandingListProductModel data = (DataModels.NPXGetVodLandingListProductModel)object;

        addTypeMask(NodeType.VOD);
        if ("product".equals(data.type)) {
            addTypeMask(NodeType.Program);
        } else if ("series".equals(data.type)) {
            addTypeMask(NodeType.Series);
            setSeriesId(data.id);
        }

        if (parent != null && parent.isPremium()) addTypeMask(NodeType.Premium);
        if (TypeUtils.toBoolean(data.isTvPlatform, false)) addTypeMask(NodeType.OnTV);
        if (TypeUtils.toBoolean(data.isApp, false)) addTypeMask(NodeType.OnApp);
        if (TypeUtils.toBoolean(data.isWebPlatform, false)) addTypeMask(NodeType.OnWeb);

        setNodeId(data.id);
        setTitle(data.title);
        setImageUrl(data.portraitImage);
        setClassificationText(data.classification);
        setRecommendationGenreId(data.genreId);

        // catId
        // classificationImage
        // description
        // displayOrder
        // isIncludeAdultCategory
        // isWeb
        // landscapeImage
        // libraryLogo
        // logo
        // portraitImage
        // productType
        // sortDisplayOrder
        // sortDisplayOrderId
        // sortId
        // sortOnAir
        // sortOnlyPopularity
        // sortOnlyPopularityId
        // sortPopularity
        // sortTitle
        // withImageTitle
        // isSubscribed
    }
}
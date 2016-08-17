package com.pccw.nowplayer.model.node.deprecated;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.model.node.NodeWrapper;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by kriz on 18/5/2016.
 */
@Deprecated
@NodeWrapper(underlyingClass=DataModels.NPXGetVodLandingPageCategoryModel.class)
public class NPXGetVodLandingPageCategoryModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodLandingPageCategoryModel data = (DataModels.NPXGetVodLandingPageCategoryModel)object;

        if (data.category != null) {
            type |= NodeType.Category;
            setTitle(data.category.catTitle);
            setNodeId(data.category.catId);
            setImageUrl(StringUtils.getAbsUrl(data.category.hdImg1Path));
        } else if (data.series != null) {
            type |= NodeType.Series;
            setPaymentType(data.series.seriesPaymentType);
            setTitle(data.series.seriesTitle);
            setNodeId(data.series.seriesId);
            setImageUrl(StringUtils.getAbsUrl(data.series.hdImg1Path));
            addSubNodes(data.series.episode);
        } else if (data.programs != null) {
            type |= NodeType.Program;
            setPaymentType(data.programs.paymentType);
            setTitle(data.programs.episodeTitle);
            setNodeId(data.programs.episodeId);
            setImageUrl(StringUtils.getAbsUrl(data.programs.hdImg1Path));
        }
    }
}

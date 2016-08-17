package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-6-19.
 */
@NodeWrapper(underlyingClass = DataModels.NPXGetLandingDataCat.class)
public class NPXGetLandingDataCatNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetLandingDataCat data = (DataModels.NPXGetLandingDataCat) object;

        addTypeMask(NodeType.LandingSection);
        if (NodeType.BANNER.equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.LandingBanner);
        } else if (NodeType.RESUME.equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.LandingResume);
        } else if (NodeType.WATCHLIST.equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.LandingWatchList);
        } else if (NodeType.LIVE.equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.LandingLive);
        } else if (NodeType.RECOMMENDATION.equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.LandingRecommendation);
        } else if (NodeType.AD.equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.AdSection);
        } else if (NodeType.VODS.equalsIgnoreCase(data.type)) {
            addTypeMask(NodeType.VOD);
        }
        setNodeId(data.name);
        setTitle(data.name);
        addSubNodes(data.data);
    }
}
package com.pccw.nowplayer.model.node;

import android.text.TextUtils;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-6-16.
 */
@NodeWrapper(underlyingClass=DataModels.NPXGetVodLandingListListModel.class)
public class NPXGetVodLandingListListModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodLandingListListModel data = (DataModels.NPXGetVodLandingListListModel)object;

        setNodeId(data.nodeId);
        setTitle(data.displayName);

        addTypeMask(NodeType.VOD);

        if (parent != null && parent.isPremium()) addTypeMask(NodeType.Premium);

        if (parent != null && !TextUtils.isEmpty(parent.getRecommendationSubGenreId())) {
            // carry forward the parent's subGenreId
            setRecommendationSubGenreId(parent.getRecommendationSubGenreId());
        }

        if (!TextUtils.isEmpty(data.nodeType)) {

            if ("category".equals(data.nodeType)) {
                addTypeMask(NodeType.Category);
            } else if ("genre".equals(data.nodeType)) {
                addTypeMask(NodeType.Genre);
            } else if ("subgenre".equals(data.nodeType)) {
                addTypeMask(NodeType.SubGenre);
            } else if ("node".equals(data.nodeType)) {
                addTypeMask(NodeType.Node);
            } else if ("featuredLibrary".equals(data.nodeType)) {
                addTypeMask(NodeType.Category | NodeType.Premium | NodeType.Cat3Parent);
            }
        } else if (parent != null) {

            if (parent.isCat3Parent()) {
                addTypeMask(NodeType.Category);
            } else if (parent.isCat3()) {
                addTypeMask(NodeType.Category);
            } else if (parent.isCategory()) {
                addTypeMask(NodeType.Category);
            } else if (parent.isGenre()) {
                addTypeMask(NodeType.Genre);
            } else if (parent.isSubGenre()) {
                addTypeMask(NodeType.SubGenre);
            } else if (parent.isNode()) {
                addTypeMask(NodeType.Node);
            } else {
                addTypeMask(NodeType.Group);
            }
        }

        addSubNodes(data.productList);
        addSubNodes(data.categoryList);
        setHasMore(true);

        // displayOrder
        // nodeTemplate
        // libraryLogo
        // numberOfIds
        // featuredInfo
    }
}
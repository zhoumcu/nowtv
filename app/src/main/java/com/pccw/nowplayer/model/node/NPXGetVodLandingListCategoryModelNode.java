package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.Is;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-6-16.
 */
@NodeWrapper(underlyingClass = DataModels.NPXGetVodLandingListCategoryModel.class)
public class NPXGetVodLandingListCategoryModelNode extends Node {

    boolean nonSubscribable;

    public boolean isSubscribable() {
        if (nonSubscribable) return false;
        return super.isSubscribable();
    }

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodLandingListCategoryModel data = (DataModels.NPXGetVodLandingListCategoryModel) object;

        addTypeMask(NodeType.VOD);
        if (parent != null) {
            if (parent.isPremium()) addTypeMask(NodeType.Premium);
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

        if (data.categoryTemplateConfig != null && data.categoryTemplateConfig.contains("Sport")) addTypeMask(NodeType.Sports);
        if (TypeUtils.toBoolean(data.isAdult, false)) addTypeMask(NodeType.AdultContent);
        setSubscribed(TypeUtils.toBoolean(data.isSubscribed, false));

        setNodeId(data.nodeId);
        setTitle(data.displayName);
        setImageUrl(data.image);
        addSubNodes(data.categoryList);
        setHasMore(true);
        setRecommendationSubGenreId(data.subId);

        nonSubscribable = Is.equal(data.nodeTemplate, "10posterPPV"); // If VE, then it is non-subscribable

        // categoryTemplateConfig
        // categoryTemplateId
        // nodeTemplate
        // rolloverMessage
        // totalNumberOfProducts
    }
}
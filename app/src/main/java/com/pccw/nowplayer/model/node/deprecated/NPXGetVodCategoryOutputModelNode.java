package com.pccw.nowplayer.model.node.deprecated;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.model.node.NodeWrapper;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Swifty on 5/21/2016.
 */
@Deprecated
@NodeWrapper(underlyingClass=DataModels.NPXGetVodCategoryOutputModel.class)
public class NPXGetVodCategoryOutputModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodCategoryOutputModel data = (DataModels.NPXGetVodCategoryOutputModel)object;

        long premiumBit = isPremiumCatalog() ? NodeType.Premium : 0L;
        type |= premiumBit;

        addSubNodes(data.categoryList);
        hasMore = true;
    }
}

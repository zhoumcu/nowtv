package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.LString;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by kriz on 18/5/2016.
 */
@NodeWrapper(underlyingClass=DataModels.NPXGetVodLandingPageOutputModel.class)
public class NPXGetVodLandingPageOutputModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodLandingPageOutputModel data = (DataModels.NPXGetVodLandingPageOutputModel)object;

        setNodeId("__on_demand_landing");
        setTitle("On Demand", "自選節目");
        addSubNodes(data.vodLandingList);
        if (subNodes != null) {
            for (Node n : subNodes) {
                n.setHasMore(false);
                if (n.isCat3Parent()) {
                    for (Node cat3 : n.getSubNodes(NodeType.Category)) {
                        cat3.removeTypeMask(NodeType.Category);
                        cat3.addTypeMask(NodeType.Cat3);
                    }
                }
            }
        }
    }
}

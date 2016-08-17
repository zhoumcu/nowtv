package com.pccw.nowplayer.model.node.deprecated;

import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.model.node.NodeWrapper;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by swifty on 28/5/2016.
 */
@Deprecated
@NodeWrapper(underlyingClass = DataModels.NPXGetVodCategoryProgramsModel.class)
public class NPXGetVodCategoryProgramsModelNode extends Node {
    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodCategoryProgramsModel data = (DataModels.NPXGetVodCategoryProgramsModel) object;

        setPaymentType(data.paymentType);
        setNodeId(data.episodeId);
        setTitle(data.episodeTitle);
        setImageUrl(StringUtils.getAbsUrl(data.hdImg1Path));
    }
}

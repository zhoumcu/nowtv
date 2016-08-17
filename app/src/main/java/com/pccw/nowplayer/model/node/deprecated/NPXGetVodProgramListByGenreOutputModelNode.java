package com.pccw.nowplayer.model.node.deprecated;

import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.model.node.NodeWrapper;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Swifty on 5/20/2016.
 */
@Deprecated
@NodeWrapper(underlyingClass = DataModels.NPXGetVodProgramListByGenreOutputModel.class)
public class NPXGetVodProgramListByGenreOutputModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodProgramListByGenreOutputModel data = (DataModels.NPXGetVodProgramListByGenreOutputModel) object;
        addSubNodes(data.programs);
    }
}

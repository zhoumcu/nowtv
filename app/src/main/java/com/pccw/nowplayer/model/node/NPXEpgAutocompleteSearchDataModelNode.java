package com.pccw.nowplayer.model.node;

import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-7-20.
 */
@NodeWrapper(underlyingClass = DataModels.NPXEpgAutocompleteSearchDataModel.class)
public class NPXEpgAutocompleteSearchDataModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXEpgAutocompleteSearchDataModel data = (DataModels.NPXEpgAutocompleteSearchDataModel) object;

        addSubNodes(data.channel);
        addSubNodes(data.movies);
        addSubNodes(data.ondemand);
        addSubNodes(data.program);
    }
}
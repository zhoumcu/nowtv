package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-7-20.
 */
@NodeWrapper(underlyingClass = DataModels.NPXEpgAutocompleteSearchMoviesModel.class)
public class NPXEpgAutocompleteSearchMoviesModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXEpgAutocompleteSearchMoviesModel data = (DataModels.NPXEpgAutocompleteSearchMoviesModel) object;

        addTypeMask(NodeType.VODProgram);
        setNodeId(data.product_id_t);
        setTitle(data.product_en_us_name_t, data.product_zh_tw_name_t);
        setImageUrl(data.category_en_us_hd_image_1_t, data.category_zh_tw_hd_image_1_t);
    }
}
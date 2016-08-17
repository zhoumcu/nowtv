package com.pccw.nowplayer.model.node;

import android.text.TextUtils;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.DeviceUtils;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.utilities.Log;

/**
 * Created by Kriz on 2016-6-19.
 */
@NodeWrapper(underlyingClass = DataModels.NPXGetLandingDataData.class)
public class NPXGetLandingDataDataNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetLandingDataData data = (DataModels.NPXGetLandingDataData) object;

        if (!TextUtils.isEmpty(data.channel)) {
            addTypeMask(NodeType.EPG);
            if (!TextUtils.isEmpty(data.productId)) {
                addTypeMask(NodeType.Program);
                setNodeId(data.productId);
            } else {
                addTypeMask(NodeType.Channel);
                setNodeId(data.channel);
            }
        } else if (!TextUtils.isEmpty(data.productId) || !TextUtils.isEmpty(data.seriesId) || !TextUtils.isEmpty(data.catId)) {
            addTypeMask(NodeType.VOD);
            if (!TextUtils.isEmpty(data.productId)) {
                addTypeMask(NodeType.Program);
                setNodeId(data.productId);
            } else if (!TextUtils.isEmpty(data.seriesId)) {
                addTypeMask(NodeType.Series);
                setNodeId(data.seriesId);
            } else if (!TextUtils.isEmpty(data.catId)) {
                addTypeMask(NodeType.Category);
                setNodeId(data.catId);
            } else {
                Log.w("Unknown type: %@", data.toString());
            }
        } else if (!TextUtils.isEmpty(data.slotId)) {
            addTypeMask(NodeType.AdBanner);
            if (DeviceUtils.isTablet(PlayerApplication.getContext())) {
                if ("android_tablet".equals(data.platform)) {
                    setNodeId(data.slotId);
                }
            } else {
                if ("android_phone".equals(data.platform)) {
                    setNodeId(data.slotId);
                }
            }
        }

        setImageUrl(data.image);
        setTitle(data.title);
        setSubtitle(data.subtitle);
        setLibraryName(data.library);
        setChannelId(data.channel);
        setSeriesId(data.seriesId);
        // setCatId(data.catId);
        setRemarks(String.format(PlayerApplication.getContext().getString(R.string.time_left), TextUtils.isEmpty(data.time) ? 0 : data.time));
        // setDesc(data.desc);
        setSubscribed(data.isSubscribed);
        // setFavorite(data.isFavorite);
    }
}
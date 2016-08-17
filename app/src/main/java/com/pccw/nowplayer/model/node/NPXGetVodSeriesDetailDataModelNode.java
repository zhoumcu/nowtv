package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

import java.util.List;

/**
 * Created by Kriz on 2016-6-17.
 */
@NodeWrapper(underlyingClass=DataModels.NPXGetVodSeriesDetailDataModel.class)
public class NPXGetVodSeriesDetailDataModelNode extends NPXGetVodMoreOptionVodModelNode {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodSeriesDetailDataModel data = (DataModels.NPXGetVodSeriesDetailDataModel) object;

        removeTypeMask(NodeType.Program);
        addTypeMask(NodeType.Series);
        setAutoPlayNextEpisodeDisabled(TypeUtils.toBoolean(data.enableNextEpisode, false));
        setTypeMask(NodeType.OnTV, TypeUtils.toBoolean(data.isTV, false));
        setTypeMask(NodeType.OnApp, TypeUtils.toBoolean(data.isApp, false));
        setNodeId(data.seriesId);
        setSeriesId(data.seriesId);
        setSeriesName(data.seriesTitle);
        setTitle(data.seriesTitle);
        setEpisodeCount(data.numOfEpisode);
        setSeasonName(data.seasonName);
        List<Node> episodes = addSubNodes(data.episode);

        boolean isVE = false;
        for (Node episode : episodes) {
            episode.addTypeMask(NodeType.Episode);

            if (episode.isVE()) {
                isVE = true;
            }
        }
        setTypeMask(NodeType.VE, isVE);

//        public String encoding;
//        public String isWeb;
//        public int numOfEpisode;
//        public String sdImg1Path;
//        public String sdImg2Path;
//        public String sdImg3Path;
//        public String sdImg4Path;
    }
}
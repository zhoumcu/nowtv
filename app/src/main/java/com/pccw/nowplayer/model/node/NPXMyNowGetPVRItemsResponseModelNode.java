package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.DateBuilder;
import com.pccw.nowplayer.utils.Is;
import com.pccw.nowplayer.utils.ListUtils;
import com.pccw.nowtv.nmaf.npx.mynow.DataModels;

import java.util.List;

/**
 * Created by Kriz on 2016-7-21.
 */
@NodeWrapper(underlyingClass = DataModels.NPXMyNowGetPVRItemsResponseModel.class)
public class NPXMyNowGetPVRItemsResponseModelNode extends Node {

    boolean failed;
    transient DateBuilder recDateFormatter;

    @Override
    public Object getAttribute(String name) {
        if (Is.equal(name, "pvrFailed")) {
            return Boolean.valueOf(failed);
        }
        return super.getAttribute(name);
    }

    public String getStatusText() {
        if (isSeries()) {
            List<Node> episodes = getEpisodes();
            for (Node episode : episodes) {
                if (episode.isLive()) {
                    return episode.getStatusText();
                } else if (episode.getStartTime().getTime() > System.currentTimeMillis()) {
                    return episode.getStatusText();
                }
            }
            return null;
        } else {
            if (recDateFormatter == null) recDateFormatter = DateBuilder.create().setEnglishFormat("d MMM h:mma");
            return recDateFormatter.setTime(getStartTime()).formatEnglish();
        }
    }

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXMyNowGetPVRItemsResponseModel data = (DataModels.NPXMyNowGetPVRItemsResponseModel) object;

        setTitle(data.progEngName, data.progChiName);
        setSeriesName(data.seriesEngName, data.seriesChiName);
        setChannelId(data.channelId);
        setLibraryName("CH " + getChannelCode()); // use channel code instead of channel name
        setSynopsis(data.progEngSynopsis, data.progChiSynopsis);
        setChannelId(data.channelId);
        setSeriesId(data.seriesId);
        setCid(data.contentId);
        setEpisodeNum(data.episodeNum == null ? 0 : data.episodeNum);
        failed = Is.equal(data.recordStage, "F");

        DateBuilder db = new DateBuilder().setEnglishFormat("yyyyMMddHHmmss");
        setStartTime(db.parseEnglish(data.recStartDate));
        setEndTime(db.parseEnglish(data.recEndDate));

        addTypeMask(NodeType.PVR | NodeType.EPG);

        if (Is.equal(data.progType, "P")) {
            addTypeMask(NodeType.Program);
        } else if (Is.equal(data.progType, "S")) {
            if (Is.notEmpty(data.contentId)) {
                addTypeMask(NodeType.Program | NodeType.Episodic);
            }
        } else if (Is.empty(data.progType) && Is.notEmpty(data.seriesId)) {
            addTypeMask(NodeType.Series);
            setTitle(data.seriesEngName, data.seriesChiName); // use series name as title!
        }

        // setRecordStage(data.recordStage);
        // setSchId(data.schId);
        // setChannelType(data.channelType);
        // setProgAutoName(data.progAutoName);
        // setProgAutoSynopsis(data.progAutoSynopsis);
        // setSchInputCreatedDate(data.schInputCreatedDate);
        // setSchInputId(data.schInputId);
        // setSeriesAutoName(data.seriesAutoName);
    }
}
package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.DateBuilder;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

/**
 * Created by Kriz on 2016-7-18.
 */
@NodeWrapper(underlyingClass = DataModels.NPXGetVodMoreOptionEpgModel.class)
public class NPXGetVodMoreOptionEpgModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodMoreOptionEpgModel data = (DataModels.NPXGetVodMoreOptionEpgModel) object;

        addTypeMask(NodeType.EPG | NodeType.Program);

        setTypeMask(NodeType.NPVR, data.isNpvrProg);
        setTypeMask(NodeType.Recordable, data.recordable);
        setTypeMask(NodeType.Episodic, data.episodic);

        setNodeId("" + data.programId);
        setCid(data.cid);
        setChannelId(data.channelId);

        setStartTime(data.actualStartTime);
        setEndTime(data.endTime);

        DateBuilder dateBuilder = new DateBuilder();
        setStartTimeText(dateBuilder.setTime(getStartTime()).formatEnglish("h:mm a"));
        setEndTimeText(dateBuilder.setTime(getEndTime()).formatEnglish("h:mm a"));
        setOffAirDate(data.expirationDay == 0 ? null : dateBuilder.setTime(data.expirationDay).formatEnglish("EEE dd MMM"));

        setTitle(data.engProgName, data.chiProgName);
        setSynopsis(data.synopsis, data.chiSynopsis);
        setSeriesName(data.engSeriesName, data.chiSeriesName);
        setEpisodeName(data.episodeName);
        setEpisodeNum(data.episodeNum);
        setImageUrl(data.portraitImage);
        setSeasonName(data.seasonName);
        setSeasonNum(data.seasonNum);
        setDuration(data.duration);
        setDurationText(data.duration == 0 ? "" : String.valueOf(data.duration));
        setHasOtherTimes(data.hasOtherTime);

        // setActualStartTime(data.actualStartTime);
        // setChannelChiName(data.channelChiName);
        // setChannelEngName(data.channelEngName);
        // setChannelId(data.channelId);
        // setChannelKey(data.channelKey);
        // setChiProgName(data.chiProgName);
        // setChiSeriesName(data.chiSeriesName);
        // setChiSynopsis(data.chiSynopsis);
        // setCid(data.cid);
        // setCreatedBy(data.createdBy);
        // setCreatedDate(data.createdDate);
        // setDuration(data.duration);
        // setEffectiveDay(data.effectiveDay);
        // setEndTime(data.endTime);
        // setEngProgName(data.engProgName);
        // setEngSeriesName(data.engSeriesName);
        // setEpisodeName(data.episodeName);
        // setEpisodeNum(data.episodeNum);
        // setEpisodic(data.episodic);
        // setExpirationDay(data.expirationDay);
        // setFirstReleaseYear(data.firstReleaseYear);
        // setGenre(data.genre);
        // setHasOtherNonAdult(data.hasOtherNonAdult);
        // setHasOtherTime(data.hasOtherTime);
        // setId(data.id);
        // setIsDeleted(data.isDeleted);
        // setIsLive(data.isLive);
        // setIsNpvrOnair(data.isNpvrOnair);
        // setIsNpvrProg(data.isNpvrProg);
        // setIsRestartTv(data.isRestartTv);
        // setOriginalLanguage(data.originalLanguage);
        // setNpvrEndTime(data.npvrEndTime);
        // setNpvrStartTime(data.npvrStartTime);
        // setPortraitImage(data.portraitImage);
        // setPremier(data.premier);
        // setProgName(data.progName);
        // setProgramId(data.programId);
        // setRecordable(data.recordable);
        // setSeasonName(data.seasonName);
        // setSeasonNum(data.seasonNum);
        // setSeriesName(data.seriesName);
        // setSubGenre(data.subGenre);
        // setSynopsis(data.synopsis);
        // setType(data.type);
        // setWithImageTiele(data.withImageTiele);
    }
}
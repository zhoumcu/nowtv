package com.pccw.nowplayer.model.node;

import android.text.format.DateFormat;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.DateBuilder;
import com.pccw.nowplayer.utils.DateUtils;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

import java.util.Date;

/**
 * Created by Kriz on 2016-7-4.
 */
@NodeWrapper(underlyingClass = DataModels.NPXEpgProgramDetailDataModel.class)
public class NPXEpgProgramDetailDataModelNode extends Node {

    public String getStatusText() {
        if (DateUtils.isToday(getStartTime())) {
            return PlayerApplication.getContext().getString(R.string.watch_today);
        } else if (DateUtils.isTomorrow(getStartTime())) {
            return PlayerApplication.getContext().getString(R.string.watch_tmr);
        } else {
            return (String) DateFormat.format("dd MMM", getStartTime());
        }
    }

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXEpgProgramDetailDataModel data = (DataModels.NPXEpgProgramDetailDataModel) object;

        DateBuilder dateBuilder = new DateBuilder();

        addTypeMask(NodeType.EPGProgram);
        setActorsText(data.actor);

        Date startDate = data.actualStartTime != 0 ? new Date(data.actualStartTime) : null;
        setStartTime(startDate);
        setStartTimeText(startDate == null ? null : dateBuilder.setTime(startDate).formatEnglish("h:mm a"));
        // setChannelChiName(data.channelChiName);
        // setChannelEngName(data.channelEngName);
        setChannelId(data.channelId);
        // setChannelKey(data.channelKey);
        setTitle(data.engProgName, data.chiProgName);
        setSeriesName(data.engSeriesName, data.chiSeriesName);
        setSynopsis(data.synopsis, data.chiSynopsis);
        setCid(data.cid);
        // setCreatedBy(data.createdBy);
        // setCreatedDate(data.createdDate);
        setDuration(data.duration);
        setDurationText(String.valueOf(data.duration));
        // setEffectiveDay(data.effectiveDay);
        Date endDate = data.endTime != 0 ? new Date(data.endTime) : null;
        setEndTime(endDate);
        setEndTimeText(endDate == null ? null : dateBuilder.setTime(endDate).formatEnglish("h:mm a"));
        setEpisodeName(data.episodeName);
        setEpisodeNum(data.episodeNum);
        setTypeMask(NodeType.Episodic, TypeUtils.toBoolean(data.episodic, false));
        // setExpirationDay(data.expirationDay);
        // setFirstReleaseYear(data.firstReleaseYear);
        // setGenre(data.genre);
        // setHasOtherNonAdult(data.hasOtherNonAdult);
        setHasOtherTimes(data.hasOtherTime);
        // setIsDeleted(data.isDeleted);
        // setIsLive(data.isLive);
        // setIsNpvrOnair(data.isNpvrOnair);
        setTypeMask(NodeType.NPVR, TypeUtils.toBoolean(data.isNpvrProg, false));
        // setIsRestartTv(data.isRestartTv);
        // setOriginalLanguage(data.originalLanguage);
        setImageUrl(data.portraitImage);
        // setPremier(data.premier);
        // setProgName(data.progName);
        setNodeId(data.programId);
        setTypeMask(NodeType.Recordable, TypeUtils.toBoolean(data.recordable, false));
        setSeasonNum(data.seasonNum);
        setSeriesName(data.seriesName);
        setSeriesId(data.sid);
        // setSubGenre(data.subGenre);
        setVodNodeId(data.vodNodeId);
        // setWithImageTitle(data.withImageTitle);
    }
}
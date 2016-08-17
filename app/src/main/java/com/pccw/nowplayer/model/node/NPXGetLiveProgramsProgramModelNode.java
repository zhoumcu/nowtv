package com.pccw.nowplayer.model.node;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

import java.util.Date;

/**
 * Created by Kriz on 2016-6-18.
 */
@NodeWrapper(underlyingClass = DataModels.NPXGetLiveProgramsProgramModel.class)
public class NPXGetLiveProgramsProgramModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetLiveProgramsProgramModel data = (DataModels.NPXGetLiveProgramsProgramModel) object;

        addTypeMask(NodeType.EPGProgram);

        setNodeId(data.vimProgramId);
        setDateText(data.date);
        setTitle(data.name);
        if (data.start != 0) setStartTime(new Date(data.start));
        setStartTimeText(data.startTime);
        if (data.end != 0) setEndTime(new Date(data.end));
        setEndTimeText(data.endTime);
        setDuration(data.duration);
        setDurationText(String.valueOf(data.duration));
        // setCc(data.cc);
        setTypeMask(NodeType.Recordable, data.recordable);
        // setNpvrStartTime(data.npvrStartTime);
        // setNpvrEndTime(data.npvrEndTime);
        // setRestartTv(data.restartTv);
        setTypeMask(NodeType.NPVR, data.npvrProg);
    }
}
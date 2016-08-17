package com.pccw.nowplayer.service;

import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.utils.gson.GsonUtil;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFMediaPlayerController;

/**
 * Created by Kevin on 2016/6/21.
 */
public abstract class MediaPlayerStatusListener implements NMAFMediaPlayerController.NMAFMediaPlayerStatusListener {

    /**
     * AlternativeStreamChanged,
     * BitrateChanged,
     * DurationChanged,
     * SubtitleChanged;
     *
     * @param infoType
     * @param i
     */
    @Override
    public void onPlaybackInformation(NMAFMediaPlayerController.InfoType infoType, int i) {
        L.d("onPlaybackInformation " + infoType + " i = " + i);
    }


    @Override
    public void onExternalDisplayDetected() {
//        L.e("onExternalDisplayDetected ");
    }

    @Override
    public void onItemPlaybackFinished(NMAFMediaPlayerController.PlaylistItem playlistItem, NMAFMediaPlayerController.FinishType finishType, Throwable throwable) {
        L.d("onItemPlaybackFinished " + " finishType = " + finishType + " playlistItem = " + GsonUtil.toJson(playlistItem));
    }


    @Override
    public void onPlaybackStreamDimensionChanged(int i, int i1) {
        L.e("onPlaybackStreamDimensionChanged  i = " + i + " i1 = " + i1);
    }

    /**
     * loading progress
     *
     * @param i
     */
    @Override
    public void onPlaybackStreamingStatusChanged(int i) {
//        L.e("onPlaybackStreamingStatusChanged  i = " + i);
    }

}

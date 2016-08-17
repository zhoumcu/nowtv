package com.pccw.nowplayer.link.handler;

import android.content.Context;
import android.os.Bundle;

public abstract class LinkHandler {
    protected LinkHandler() {
    }

    public abstract boolean handlerLink(Context context, String link, String link_prefix, String link_suffix, Bundle bundle);

}